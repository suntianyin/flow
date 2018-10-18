package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.OrgDao;
import com.apabi.flow.admin.model.Org;
import com.apabi.flow.admin.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgServiceImpl implements OrgService {
    @Autowired
    private OrgDao orgDao;

    @Override
    public List<Org> queryAll() {
        return orgDao.queryAll();
    }

    @Override
    public Org getById(Integer id){
        return orgDao.getById(id);
    }
}
