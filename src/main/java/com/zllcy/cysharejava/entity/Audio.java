package com.zllcy.cysharejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zllcy
 * @date 2022/3/30 21:56
 */
@Data
@AllArgsConstructor
public class Audio {
    Integer id;

    String username;

    String filename;

    String song;

    String singer;

    String style;

    String language;

    String year;

    String description;
}
