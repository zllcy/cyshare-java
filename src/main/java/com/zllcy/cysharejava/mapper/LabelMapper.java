package com.zllcy.cysharejava.mapper;

import com.zllcy.cysharejava.entity.Label;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabelMapper {
    List<Label> listLabel();

    int insertLabel(Label label);

    Label searchLabel(String name);
}
