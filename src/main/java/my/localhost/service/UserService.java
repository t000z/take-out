package my.localhost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.User;

public interface UserService extends IService<User> {
    boolean sendEmail(String toEmail, String text, String message);
}
