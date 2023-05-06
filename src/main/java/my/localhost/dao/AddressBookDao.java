package my.localhost.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.localhost.domain.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {
}
