package com.zllcy.cysharejava.service.impl;

import com.zllcy.cysharejava.entity.Singer;
import com.zllcy.cysharejava.mapper.SingerMapper;
import com.zllcy.cysharejava.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingerServiceImpl implements SingerService {
    @Autowired
    private SingerMapper singerMapper;

    @Override
    public List<Singer> listSinger() {
        return singerMapper.listSinger();
    }
}
