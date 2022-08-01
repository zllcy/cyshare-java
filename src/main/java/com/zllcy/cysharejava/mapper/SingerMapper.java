package com.zllcy.cysharejava.mapper;

import com.zllcy.cysharejava.entity.Singer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SingerMapper {
    List<Singer> listSinger();

    int insertSinger(Singer singer);

    Singer searchSinger(String name);
}
