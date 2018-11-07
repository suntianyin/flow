package com.apabi.flow.systemconf.service.impl;

import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.model.SystemConf;
import com.apabi.flow.systemconf.service.SystemConfService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: sunty
 * @date: 2018/11/07 10:20
 * @description:
 */
@Service
public class SystemConfServiceImpl implements SystemConfService {
    @Autowired
    SystemConfMapper systemConfMapper;
    @Override
    public Page<SystemConf> queryPage() {
        return systemConfMapper.queryPage();
    }

    @Override
    public int addSystemConf(SystemConf systemConf) {
        return systemConfMapper.insert(systemConf);
    }

    @Override
    public SystemConf selectdataById(String id) {
        return systemConfMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateSystemConf(SystemConf systemConf) {
        return systemConfMapper.updateByPrimaryKey(systemConf);
    }

    @Override
    public int deleteByPrimaryKey(String id) {
        return systemConfMapper.deleteByPrimaryKey(id);
    }
}
