package my.localhost.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.localhost.domain.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorDao extends BaseMapper<DishFlavor> {
}
