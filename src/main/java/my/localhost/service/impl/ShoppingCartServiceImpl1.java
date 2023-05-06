package my.localhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.common.BaseContext;
import my.localhost.dao.ShoppingCartDao;
import my.localhost.domain.ShoppingCart;
import my.localhost.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingCartServiceImpl1 extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {

    @Override
    @Transactional
    public void saveDishOrSetmeal(ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContext.getId());

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        LambdaUpdateWrapper<ShoppingCart> luw = new LambdaUpdateWrapper<>();
        lqw.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        luw.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        // 判断传入的是套餐还是菜品
        if (shoppingCart.getDishId() != null) {
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
            luw.eq(ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            luw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart sc = this.getOne(lqw);
        if (sc != null) {
            luw.set(ShoppingCart::getNumber, sc.getNumber() + 1);
            this.update(luw);
        } else {
            this.save(shoppingCart);
        }
    }
}
