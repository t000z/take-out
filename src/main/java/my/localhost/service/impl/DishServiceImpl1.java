package my.localhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import my.localhost.dao.DishDao;
import my.localhost.domain.Dish;
import my.localhost.domain.DishFlavor;
import my.localhost.dto.DishDto;
import my.localhost.service.CategoryService;
import my.localhost.service.DishFlavorService;
import my.localhost.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl1 extends ServiceImpl<DishDao, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Override
    @Transactional
    @CacheEvict(value = "DishDto", key = "#p0.categoryId")
    public void dishWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> dishFlavors = dishDto.getFlavors().stream()
                .peek((d) -> d.setDishId(dishId))
                .collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    @Cacheable(value = "DishDto", key = "'_' + #p0", unless = "#result == null")
    public DishDto getByIdWithFlavor(Long id) {
        DishDto result = new DishDto();
        BeanUtils.copyProperties(this.getById(id), result);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, result.getId())
                .select(DishFlavor::getId, DishFlavor::getName, DishFlavor::getValue)
                .orderByAsc(DishFlavor::getId);
        result.setFlavors(dishFlavorService.list(lqw));

        return result;
    }

    @Override
    public Page<DishDto> pageWithFlavor(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lqw.orderByDesc(Dish::getCreateTime);
        this.page(pageInfo, lqw);

        // 对象拷贝，忽略数据列表
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // 构建Category_id到Category_name的映射
        List<Dish> source = pageInfo.getRecords();
        Set<Long> ids = source.stream()
                .map((item) -> item.getCategoryId())
                .collect(Collectors.toSet());
        Map<Long, String> idAndNameMap = categoryService.idMapName(ids);

        // 使用上面构建的映射构建DishDtoList
        List<DishDto> target = source.stream().map((item) -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            dto.setCategoryName(idAndNameMap.get(item.getCategoryId()));
            return dto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(target);

        return dishDtoPage;
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "DishDto", key = "#p0.categoryId"),
        @CacheEvict(value = "DishDto", key = "'_' + #p0.id")
    })
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> dfLqw = new LambdaQueryWrapper<>();
        dfLqw.select(DishFlavor::getId).eq(DishFlavor::getDishId, dishDto.getId());
        List<DishFlavor> oldDishFlavors = dishFlavorService.list(dfLqw);
        List<DishFlavor> newDishFlavors = dishDto.getFlavors().stream()
                .peek((item) -> item.setDishId(dishDto.getId()))
                .collect(Collectors.toList());

        if (oldDishFlavors.size() < newDishFlavors.size()) {
            // 新链表比旧链表长，说明要新增数据，新增数据从新链表中移除
            Set<Long> ids = oldDishFlavors.stream().map((item) -> item.getId()).collect(Collectors.toSet());
            List<DishFlavor> saveObjs = new Vector<>();
            for (int i = 0; i < newDishFlavors.size(); i++) {
                if (newDishFlavors.get(i).getId() == null
                        || !ids.contains(newDishFlavors.get(i).getId())) {
                    saveObjs.add(newDishFlavors.remove(i));
                    i--;  // 移除元素后，指针不前移
                }
            }
            dishFlavorService.saveBatch(saveObjs);
        } else if (oldDishFlavors.size() > newDishFlavors.size()) {
            // 旧链表比新链表长，说明要移除数据
            Set<Long> ids = newDishFlavors.stream().map((item) -> item.getId()).collect(Collectors.toSet());
            List<Long> delIds = oldDishFlavors.stream().filter((item) -> !ids.contains(item.getId()))
                    .map((item) -> item.getId())
                    .collect(Collectors.toList());
            dishFlavorService.removeByIds(delIds);
        }

        dishFlavorService.updateBatchById(newDishFlavors);
    }

    @Override
    @Cacheable(value = "DishDto", key = "#p0", unless = "#result == null")
    public List<DishDto> listDishDto(Long categoryId) {

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, categoryId).eq(Dish::getStatus, 1).orderByAsc(Dish::getSort);
        List<Dish> dishes = this.list(lqw);

        List<DishDto> dishDtos = dishes.stream()
                .map((item) -> {
                    DishDto dto = new DishDto();
                    BeanUtils.copyProperties(item, dto);

                    LambdaQueryWrapper<DishFlavor> dfLqw = new LambdaQueryWrapper();
                    dfLqw.eq(DishFlavor::getDishId, item.getId())
                            .select(DishFlavor::getId, DishFlavor::getName, DishFlavor::getValue)
                            .orderByAsc(DishFlavor::getId);

                    dto.setFlavors(dishFlavorService.list(dfLqw));

                    return dto;
                }).collect(Collectors.toList());

        return dishDtos;
    }
}
