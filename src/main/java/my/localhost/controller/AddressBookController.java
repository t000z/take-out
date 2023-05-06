package my.localhost.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.BaseContext;
import my.localhost.common.R;
import my.localhost.domain.AddressBook;
import my.localhost.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        log.info("用户添加地址：{}", addressBook);
        addressBookService.saveCheckIsDefault(addressBook);
        return R.success("地址添加成功");
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBook.setIsDefault(1);
        addressBookService.updateCheckIsDefault(addressBook);
        return R.success("修改默认地址成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getIsDefault, 1);
        lqw.eq(AddressBook::getUserId, BaseContext.getId());
        AddressBook addressBook = addressBookService.getOne(lqw);
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateCheckIsDefault(addressBook);
        return R.success("地址修改成功");
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        addressBookService.removeById(id);
        return R.success("地址删除成功");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getUserId, BaseContext.getId());
        List<AddressBook> addressBooks = addressBookService.list(lqw);
        return R.success(addressBooks);
    }
}
