package com.zllcy.cysharejava.service;

import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.common.Result;
import com.zllcy.cysharejava.entity.User;

/**
 * @author zllcy
 * @date 2022/3/30 22:36
 */
public interface UserService {
    /**
     * 分页数量
     */
    int PAGE_SIZE = 5;

    Result login(String username, String password);

    Result register(User user);

    PageInfo<User> listUserForPage(Integer pageNum);

    int deleteUser(Integer id);

    int updateUser(User user);

    Result searchUserById(Integer id);

    Result searchUserByName(String username);



}
