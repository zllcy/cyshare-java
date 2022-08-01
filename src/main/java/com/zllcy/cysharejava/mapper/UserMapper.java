package com.zllcy.cysharejava.mapper;

import com.zllcy.cysharejava.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zllcy
 * @date 2022/3/30 22:36
 */
@Mapper
public interface UserMapper {
    User searchUser(String username);

    int insertUser(User user);

    List<User> listUser();

    int deleteUser(Integer id);

    int updateUser(User user);

    User searchUserById(Integer id);

    User searchUserByName(String username);

}
