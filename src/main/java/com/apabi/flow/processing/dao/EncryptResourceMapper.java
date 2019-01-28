package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.EncryptResource;
import com.github.pagehelper.Page;

public interface EncryptResourceMapper {
    int deleteByPrimaryKey(String id);

    int insert(EncryptResource record);

    int insertSelective(EncryptResource record);

    EncryptResource selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EncryptResource record);

    int updateByPrimaryKey(EncryptResource record);

    Page<EncryptResource> selectByEncryptId(String id);
}