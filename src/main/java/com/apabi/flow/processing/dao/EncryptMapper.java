package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.Encrypt;
import com.github.pagehelper.Page;

public interface EncryptMapper {
    int deleteByPrimaryKey(String id);

    int insert(Encrypt record);

    int insertSelective(Encrypt record);

    Encrypt selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Encrypt record);

    int updateByPrimaryKey(Encrypt record);

    Page<Encrypt> pageFind();

    Encrypt selectByBatch(String batch);
}