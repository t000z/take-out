package my.localhost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import my.localhost.domain.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void updateCheckIsDefault(AddressBook addressBook);

    void saveCheckIsDefault(AddressBook addressBook);
}
