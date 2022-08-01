package com.zllcy.cysharejava.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.entity.User;
import com.zllcy.cysharejava.mapper.UserMapper;
import com.zllcy.cysharejava.common.Result;
import com.zllcy.cysharejava.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zllcy
 * @date 2022/4/12 9:33
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(String username, String password) {
        User user = userMapper.searchUser(username);
        // 判断用户是否存在
        if (user == null) {
           return Result.fail("用户不存在");
        }

        // 判断密码是否正确
        if (!user.getPassword().equals(SecureUtil.md5(password))) {
            return Result.fail("密码不正确");
        }

        return Result.success(user);
    }

    @Override
    public Result register(User user) {
        User user2 = userMapper.searchUser(user.getUsername());
        if (user2 != null) {
            return Result.fail("用户名已存在");
        }
        // 密码进行加密
        String password2 = SecureUtil.md5(user.getPassword());
        user.setPassword(password2);
        userMapper.insertUser(user);
        return Result.success(null);
    }

    /**
     * 分页显示所有用户
     * @param pageNum
     * @return
     */
    @Override
    public PageInfo<User> listUserForPage(Integer pageNum) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<User> userList = userMapper.listUser();
        PageInfo<User> userPageInfo = new PageInfo<>(userList);
        return userPageInfo;
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteUser(id);
    }


    @Override
    public int updateUser(User user) {
        // 密码进行加密
        String password2 = SecureUtil.md5(user.getPassword());
        user.setPassword(password2);
        return userMapper.updateUser(user);
    }

    @Override
    public Result searchUserById(Integer id) {
        User user = userMapper.searchUserById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user);
    }

    @Override
    public Result searchUserByName(String username) {
        User user = userMapper.searchUserByName(username);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user);
    }


}
