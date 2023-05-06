package my.localhost.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.R;
import my.localhost.domain.Dish;
import my.localhost.domain.DishFlavor;
import my.localhost.dto.CategoryDto;
import my.localhost.dto.DishDto;
import my.localhost.service.DishFlavorService;
import my.localhost.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        log.info("新增菜品 {}", dishDto);
        dishService.dishWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={} size={}, name={}", page, pageSize, name);
        Page<DishDto> dishDtoPage = dishService.pageWithFlavor(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId) {
        List<DishDto> dishDtos = dishService.listDishDto(categoryId);
        return R.success(dishDtos);
    }
}
