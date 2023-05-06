package my.localhost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.dao.OrderDetailDao;
import my.localhost.domain.OrderDetail;
import my.localhost.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {

}