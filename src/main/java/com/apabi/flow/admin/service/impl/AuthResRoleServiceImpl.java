package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthResRoleDao;
import com.apabi.flow.admin.model.AuthResRole;
import com.apabi.flow.admin.service.AuthResRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthResRoleServiceImpl implements AuthResRoleService {

    @Autowired
    private AuthResRoleDao resRoleDao;

    @Override
    public int save(AuthResRole authResRole) {
        return resRoleDao.save(authResRole);
    }

    @Override
    public int getCountByRoleId(Integer roleId) {
        return resRoleDao.getCountByRoleId(roleId);
    }

    @Override
    public int getCountByResId(Integer resId) {
        return resRoleDao.getCountByResId(resId);
    }

    @Override
    public List<AuthResRole> getResIdByRoleId(Integer roleId) {
        return resRoleDao.getResIdByRoleId(roleId);
    }

    @Override
    public int deleteByRoleId(Integer roleId) {
        return resRoleDao.deleteByRoleId(roleId);
    }
}
