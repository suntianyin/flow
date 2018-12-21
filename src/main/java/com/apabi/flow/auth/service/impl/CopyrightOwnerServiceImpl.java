package com.apabi.flow.auth.service.impl;

import com.apabi.flow.auth.dao.CopyrightOwnerMapper;
import com.apabi.flow.auth.model.CopyrightOwner;
import com.apabi.flow.auth.service.CopyrightOwnerService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CopyrightOwnerServiceImpl implements CopyrightOwnerService {
    @Autowired
    CopyrightOwnerMapper copyrightOwnerMapper;

    @Override
    public int deleteByPrimaryKey(String id) {
        return copyrightOwnerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(CopyrightOwner record) {
        return copyrightOwnerMapper.insert(record);
    }

    @Override
    public int insertSelective(CopyrightOwner record) {
        return copyrightOwnerMapper.insertSelective(record);
    }

    @Override
    public CopyrightOwner selectByPrimaryKey(String id) {
        return copyrightOwnerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(CopyrightOwner record) {
        return copyrightOwnerMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(CopyrightOwner record) {
        return copyrightOwnerMapper.updateByPrimaryKey(record);
    }

    @Override
    public Page<CopyrightOwner> listCopyrightOwner(Map<String, Object> paramsMap) {
        return copyrightOwnerMapper.listCopyrightOwner(paramsMap);
    }

    @Override
    public List<CopyrightOwner> findAll() {
        return copyrightOwnerMapper.findAll();
    }
}
