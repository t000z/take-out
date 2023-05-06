package my.localhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.common.CustomException;
import my.localhost.dao.CategoryDao;
import my.localhost.domain.Category;
import my.localhost.domain.Dish;
import my.localhost.domain.Setmeal;
import my.localhost.dto.CategoryDto;
import my.localhost.service.CategoryService;
import my.localhost.service.DishService;
import my.localhost.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl1 extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CacheManager cacheManager;

    @Override
    @CacheEvict(value = "CategoryDto", key = "#result")
    public Integer remove(Long id) {
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId, id);
        if (dishService.count(lqw1) != 0) {
            throw new CustomException("分类中关联了菜品，删除失败");
        }

        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(lqw2) != 0) {
            throw new CustomException("分类中关联了套餐，删除失败");
        }

        Integer type = this.getById(id).getType();
        super.removeById(id);

        return type;
    }

    @Override
    public Map<Long, String> idMapName(Set<Long> ids) {
        LambdaQueryWrapper<Category> cLqw = new LambdaQueryWrapper<>();
        cLqw.select(Category::getId, Category::getName).in(Category::getId, ids);
        List<Category> categories = this.list(cLqw);
        Map<Long, String> idAndNameMap = new HashMap<>();
        for (Category category: categories) {
            idAndNameMap.put(category.getId(), category.getName());
        }

        return idAndNameMap;
    }

    @Override
    @Cacheable(value = "CategoryDto", key = "#p0", unless = "#result == null")
    public List<CategoryDto> getTypeList(int type) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime)
                .select(Category::getId, Category::getName)
                .eq(Category::getType, type);
        List<Category> list = this.list(lqw);

        List<CategoryDto> categoryDtos = list.stream()
                .map((a) -> new CategoryDto(a.getId(), a.getName()))
                .collect(Collectors.toList());

        return categoryDtos;
    }

    @Override
    @CacheEvict(value = "CategoryDto", key = "#result")
    public Integer updateWithCache(Category category) {
        Integer type = this.getById(category.getId()).getType();
        this.updateById(category);
        return type;
    }

    @Override
    @CacheEvict(value = "CategoryDto", key = "#p0.type")
    public void saveWithCache(Category category) {
        this.save(category);
    }
}
