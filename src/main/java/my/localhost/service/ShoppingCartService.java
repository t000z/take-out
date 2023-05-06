package my.localhost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void saveDishOrSetmeal(ShoppingCart shoppingCart);
}
