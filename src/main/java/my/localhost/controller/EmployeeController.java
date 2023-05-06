package my.localhost.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.BaseContext;
import my.localhost.common.R;
import my.localhost.domain.Employee;
import my.localhost.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 根据页面提交的用户名username查询数据库，username唯一
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        // 如果没有查询到则返回登录失败结果
        if (emp == null) {
            R.error("用户不存在");
        }

        // 密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            R.error("密码错误");
        }

        // 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            R.error("账户已禁用");
        }

        // 登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> add(@RequestBody Employee employee) {
        log.info("记录员工信息 {}", employee);

        if (employee.getPassword() == null) {
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        } else {
            employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes(StandardCharsets.UTF_8)));
        }

        employeeService.save(employee);

        return R.success("更新成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        Page pageInfo = new Page<>(page, pageSize);
        log.info("page={} size={}, name={}", page, pageSize, name);

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        lqw.orderByDesc(Employee::getCreateTime);

        employeeService.page(pageInfo, lqw);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> upData(@RequestBody Employee employee) {
        employeeService.updateById(employee);

        return R.success("信息更新成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
