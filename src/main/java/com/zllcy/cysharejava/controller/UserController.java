package com.zllcy.cysharejava.controller;

import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.common.LoginDto;
import com.zllcy.cysharejava.common.Result;
import com.zllcy.cysharejava.entity.User;
import com.zllcy.cysharejava.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author zllcy
 * @date 2022/4/12 10:26
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getUsername(), loginDto.getPassword());
    }

    @PostMapping("/register")
    public Result register (@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/listUser/{pageNum}")
    public Result listUserForPage(@PathVariable("pageNum") Integer pageNum) {
        PageInfo<User> userPageInfo = userService.listUserForPage(pageNum);
        return Result.success(userPageInfo);
    }

    @GetMapping("/searchUserById/{id}")
    public Result searchUserById(@PathVariable("id") Integer id) {
        return userService.searchUserById(id);
    }

    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        int i = userService.updateUser(user);
        if (i != 1) {
            return Result.fail("修改失败");
        }
        return Result.success(null);
    }

    @DeleteMapping("/deleteUser/{id}")
    public Result deleteUser(@PathVariable("id") Integer id) {
        int i = userService.deleteUser(id);
        if (i != 1) {
            return Result.fail("删除失败");
        }
        return Result.success(null);
    }

    @GetMapping("/searchUserByName/{username}")
    public Result searchUserByName(@PathVariable("username") String username) {
        return userService.searchUserByName(username);
    }

}
