package my.localhost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.R;
import my.localhost.dto.SetmealDto;
import my.localhost.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        log.info("添加套餐: {}", setmealDto);
        setmealService.SetmealWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={} size={}, name={}", page, pageSize, name);
        Page<SetmealDto> setmealDto = setmealService.pageWithDish(page, pageSize, name);
        return R.success(setmealDto);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        return R.success(setmealService.getByIdWithDish(id));
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("套餐修改成功");
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        setmealService.deleteWithDish(id);
        return R.success("套餐删除成功");
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(Long categoryId) {
        List<SetmealDto> setmealDtos = setmealService.getByCategoryIdsWithDish(categoryId);
        return R.success(setmealDtos);
    }
}
