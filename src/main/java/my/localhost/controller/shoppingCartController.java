package my.localhost.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.BaseContext;
import my.localhost.common.R;
import my.localhost.domain.ShoppingCart;
import my.localhost.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class shoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.saveDishOrSetmeal(shoppingCart);
        return R.success("添加购物车成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaUpdateWrapper<ShoppingCart> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getId())
                .orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);
        return R.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaUpdateWrapper<ShoppingCart> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getId());

        shoppingCartService.remove(lqw);
        return R.success("成功清空");
    }
}
