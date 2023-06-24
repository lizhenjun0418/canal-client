package top.javatool.canal.example.controller;

import org.springframework.web.bind.annotation.*;
import top.javatool.canal.example.domin.User;
import top.javatool.canal.example.dto.R;
import top.javatool.canal.example.service.IUserService;

import javax.annotation.Resource;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/insert")
    public R<Integer> insert(@RequestBody User user) {
        int row = userService.insert(user);
        return row > 0 ? R.ok() : R.fail();
    }

    @PostMapping("/update")
    public R<Integer> update(@RequestBody User user) {
        int row = userService.update(user);
        return row > 0 ? R.ok() : R.fail();
    }

    @GetMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return R.ok();
    }

    @GetMapping("executeSql")
    public R<?> executeSql(@RequestParam("sql") String sql) {
        userService.executeSql(sql);
        return R.ok();
    }
}
