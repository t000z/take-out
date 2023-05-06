package my.localhost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.dao.UserDao;
import my.localhost.domain.User;
import my.localhost.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl1 extends ServiceImpl<UserDao, User> implements UserService {
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String toEmail, String text, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();//设置发件邮箱
        simpleMailMessage.setFrom(fromEmail);//收件人邮箱
        simpleMailMessage.setTo(toEmail);//主题标题
        simpleMailMessage.setSubject(text);//信息内容
        simpleMailMessage.setText(message);//执行发送
        try {//发送可能失败
            javaMailSender.send(simpleMailMessage);
            return true;  //没有异常返回true，表示发送成功
        }catch (Exception e) {
            return false;  //发送失败，返回faLse
        }

    }
}
