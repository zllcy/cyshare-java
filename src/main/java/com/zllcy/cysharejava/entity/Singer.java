package com.zllcy.cysharejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Singer {
    private Integer id;

    private String name;

    public Singer(String singerName) {
        this.name = singerName;
    }
}
