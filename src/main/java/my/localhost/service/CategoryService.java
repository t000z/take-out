package my.localhost.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.Category;
import my.localhost.dto.CategoryDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CategoryService extends IService<Category> {
    Integer remove(Long id);

    Map<Long, String> idMapName(Set<Long> ids);

    List<CategoryDto> getTypeList(int type);

    Integer updateWithCache(Category category);

    void saveWithCache(Category category);
}
