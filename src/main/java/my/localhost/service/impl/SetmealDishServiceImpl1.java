package my.localhost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.dao.SetmealDishDao;
import my.localhost.domain.SetmealDish;
import my.localhost.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl1 extends ServiceImpl<SetmealDishDao, SetmealDish> implements SetmealDishService {
}
