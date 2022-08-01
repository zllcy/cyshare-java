package com.zllcy.cysharejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Label {
    private Integer id;

    private String name;

    public Label(String labelName) {
        this.name = labelName;
    }
}
