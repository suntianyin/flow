package com.apabi.flow.systemconf.dao;

import com.apabi.flow.systemconf.model.SystemConf;
import com.github.pagehelper.Page;

public interface SystemConfMapper {
    int deleteByPrimaryKey(String id);

    int insert(SystemConf record);

    int insertSelective(SystemConf record);

    SystemConf selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SystemConf record);

    int updateByPrimaryKey(SystemConf record);

    SystemConf selectByConfKey(String confKey);

    Page<SystemConf> queryPage();
}