package my.localhost.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.localhost.domain.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {

}