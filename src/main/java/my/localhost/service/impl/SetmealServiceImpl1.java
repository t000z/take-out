package my.localhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import my.localhost.dao.SetmealDao;
import my.localhost.domain.*;
import my.localhost.dto.SetmealDto;
import my.localhost.service.CategoryService;
import my.localhost.service.SetmealDishService;
import my.localhost.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl1 extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    @CacheEvict(value = "SetmealDto", key = "#p0.categoryId")
    public void SetmealWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream()
                .peek((item) -> item.setSetmealId(setmealId))
                .collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Cacheable(value = "SetmealDto", key = "'_' + #p0", unless = "#result == null")
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = this.setmealToSetmealDto(setmeal);
        return setmealDto;
    }

    @Override
    public Page<SetmealDto> pageWithDish(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> sLqw = new LambdaQueryWrapper<>();
        sLqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name).orderByAsc(Setmeal::getCreateTime);
        this.page(setmealPage, sLqw);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        // 构建Category_id到Category_name的映射
        List<Setmeal> source = setmealPage.getRecords();
        Set<Long> ids = source.stream()
                .map((item) -> item.getCategoryId())
                .collect(Collectors.toSet());
        Map<Long, String> idAndNameMap = categoryService.idMapName(ids);

        // 使用上面构建的映射构建DishDtoList
        List<SetmealDto> target = source.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            dto.setCategoryName(idAndNameMap.get(item.getCategoryId()));
            return dto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(target);
        return setmealDtoPage;
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "SetmealDto", key = "#p0.categoryId"),
        @CacheEvict(value = "SetmealDto", key = "'_' + #p0.id")
    })
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, setmealDto.getId()).orderByAsc(SetmealDish::getId);
        List<SetmealDish> oldSetmealDishes = setmealDishService.list(lqw);
        List<SetmealDish> newSetmealDishes = setmealDto.getSetmealDishes().stream()
                .peek((item) -> item.setSetmealId(setmealDto.getId()))
                .collect(Collectors.toList());

        if (oldSetmealDishes.size() < newSetmealDishes.size()) {
            // 新链表比旧链表长，说明要新增数据，新增数据从新链表中移除
            Set<Long> ids = oldSetmealDishes.stream().map((item) -> item.getId()).collect(Collectors.toSet());
            List<SetmealDish> saveObjs = new Vector<>();
            for (int i = 0; i < newSetmealDishes.size(); i++) {
                if (newSetmealDishes.get(i).getId() == null
                        || !ids.contains(newSetmealDishes.get(i).getId())) {
                    saveObjs.add(newSetmealDishes.remove(i));
                    i--;  // 移除元素后，指针不前移
                }
            }
            setmealDishService.saveBatch(saveObjs);
        } else if (oldSetmealDishes.size() > newSetmealDishes.size()) {
            // 旧链表比新链表长，说明要移除数据
            Set<Long> ids = newSetmealDishes.stream().map((item) -> item.getId()).collect(Collectors.toSet());
            List<Long> delIds = oldSetmealDishes.stream().filter((item) -> !ids.contains(item.getId()))
                    .map((item) -> item.getId())
                    .collect(Collectors.toList());
            setmealDishService.removeByIds(delIds);
        }

        setmealDishService.updateBatchById(newSetmealDishes);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "SetmealDto", key = "#result"),
            @CacheEvict(value = "SetmealDto", key = "'_' + #p0")
    })
    public Long deleteWithDish(Long id) {
        LambdaQueryWrapper<Setmeal> sLqw = new LambdaQueryWrapper<>();
        sLqw.select(Setmeal::getCategoryId).eq(Setmeal::getId, id);
        Setmeal setmeal = this.getOne(sLqw);

        this.removeById(id);
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(lqw);

        return setmeal.getCategoryId();
    }

    @Override
    @Cacheable(value = "SetmealDto", key = "#p0", unless = "#result == null")
    public List<SetmealDto> getByCategoryIdsWithDish(Long categoryId) {
        List<SetmealDto> setmealDtos = null;

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId, categoryId);
        List<Setmeal> setmeals = this.list(lqw);

        setmealDtos = setmeals.stream()
                .map((item) -> this.setmealToSetmealDto(item))
                .collect(Collectors.toList());

        return setmealDtos;
    }

    private SetmealDto setmealToSetmealDto(Setmeal setmeal) {
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> sdLqw = new LambdaQueryWrapper<>();
        sdLqw.eq(SetmealDish::getSetmealId, setmeal.getId())
                .select(SetmealDish::getId, SetmealDish::getDishId,
                        SetmealDish::getName, SetmealDish::getCopies, SetmealDish::getPrice)
                .orderByAsc(SetmealDish::getId);
        List<SetmealDish> setmealDishes = setmealDishService.list(sdLqw);

        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }
}
