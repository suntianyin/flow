package com.apabi.flow.systemconf.service;

import com.apabi.flow.systemconf.model.SystemConf;
import com.github.pagehelper.Page;

public interface SystemConfService {

    //分页查询
    Page<SystemConf> queryPage();

    //添加数据
    int addSystemConf(SystemConf systemConf);

    //根据id查询数据
    SystemConf selectdataById(String id);

    //编辑数据
    int updateSystemConf(SystemConf systemConf);

    int deleteByPrimaryKey(String id);


}
