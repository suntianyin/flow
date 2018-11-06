package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.SystemConf;

public interface SystemConfMapper {
    int deleteByPrimaryKey(String id);

    int insert(SystemConf record);

    int insertSelective(SystemConf record);

    SystemConf selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SystemConf record);

    int updateByPrimaryKey(SystemConf record);

    SystemConf selectByConfKey(String confkey);
}