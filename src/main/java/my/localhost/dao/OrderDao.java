package my.localhost.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.localhost.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<Orders> {

}