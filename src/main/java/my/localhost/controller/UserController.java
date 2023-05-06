package my.localhost.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.R;
import my.localhost.domain.User;
import my.localhost.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/emailCode")
    public R<String> emailCode(String toMail) {
        Random random = new Random();
        int code = 1000 + random.nextInt(8999);
        log.info("验证码: {}", code);
        redisTemplate.opsForValue().set(toMail, Integer.toString(code), 5, TimeUnit.MINUTES);
        if (userService.sendEmail(toMail, "验证码", "您的验证码为：" + code)) {
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(String toMail, Integer code, HttpSession session) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        lqw.eq(User::getEmail, toMail);
        User user = userService.getOne(lqw);

        if (user == null) {
            return R.error("该邮箱对应账户不存在");
        } else if (user.getStatus() != 1) {
            return R.error("该账户被禁用");
        }

        Integer checkCode = Integer.valueOf((String) redisTemplate.opsForValue().get(toMail));

        if (code == null || (int) code != checkCode) {
            return R.error("验证码错误");
        }

        redisTemplate.delete(toMail);
        session.setAttribute("user", user.getId());
        return R.success(user);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }

    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        userService.save(user);
        return R.success("注册成功");
    }
}
