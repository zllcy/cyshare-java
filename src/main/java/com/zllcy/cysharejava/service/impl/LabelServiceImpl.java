package com.zllcy.cysharejava.service.impl;

import com.zllcy.cysharejava.entity.Label;
import com.zllcy.cysharejava.mapper.LabelMapper;
import com.zllcy.cysharejava.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public List<Label> listLabel() {
        return labelMapper.listLabel();
    }

}
