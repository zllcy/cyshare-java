package com.zllcy.cysharejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zllcy
 * @date 2022/3/28 15:05
 */
@Data
@AllArgsConstructor
public class User {
    private Integer id;

    private String username;

    private String password;

    private String type;
}
