package my.localhost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.dao.DishFlavorDao;
import my.localhost.domain.DishFlavor;
import my.localhost.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl1 extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
