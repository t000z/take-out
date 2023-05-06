package my.localhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import my.localhost.common.BaseContext;
import my.localhost.dao.AddressBookDao;
import my.localhost.domain.AddressBook;
import my.localhost.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBookServiceImpl1 extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {
    @Override
    @Transactional
    public void updateCheckIsDefault(AddressBook addressBook) {
        if (addressBook.getIsDefault() != null) {
            LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();
            luw.eq(AddressBook::getUserId, BaseContext.getId())
                    .eq(AddressBook::getIsDefault, 1)
                    .set(AddressBook::getIsDefault, 0);
            this.update(luw);
        }

        this.updateById(addressBook);
    }

    @Override
    @Transactional
    public void saveCheckIsDefault(AddressBook addressBook) {
        if (addressBook.getIsDefault() == 1) {
            LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();
            luw.eq(AddressBook::getUserId, BaseContext.getId())
                    .eq(AddressBook::getIsDefault, 1)
                    .set(AddressBook::getIsDefault, 0);
            this.update(luw);
        }
        this.save(addressBook);
    }
}
