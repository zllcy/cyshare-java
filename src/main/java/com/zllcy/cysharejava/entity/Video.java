package com.zllcy.cysharejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zllcy
 * @date 2022/3/30 21:56
 */
@Data
@AllArgsConstructor
public class Video {
    Integer id;

    String username;

    String filename;

    String title;

    String category;

    String label;

    String language;

    String year;

    String description;

    String cover;
}
