package my.localhost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.Setmeal;
import my.localhost.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void SetmealWithDish(SetmealDto setmealDto);

    SetmealDto getByIdWithDish(Long id);

    Page<SetmealDto> pageWithDish(int page, int pageSize, String name);

    void updateWithDish(SetmealDto setmealDto);

    Long deleteWithDish(Long id);

    List<SetmealDto> getByCategoryIdsWithDish(Long categoryId);
}
