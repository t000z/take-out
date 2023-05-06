package my.localhost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.dao.EmployeeDao;
import my.localhost.domain.Employee;
import my.localhost.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl1 extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {
}
