package my.localhost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.Dish;
import my.localhost.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    void dishWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    Page<DishDto> pageWithFlavor(int page, int pageSize, String name);

    void updateWithFlavor(DishDto dishDto);

    List<DishDto> listDishDto(Long categoryId);
}
