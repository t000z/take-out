package my.localhost.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.localhost.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishDao extends BaseMapper<Dish> {
}
