package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthRoleGroupDao;
import com.apabi.flow.admin.model.AuthRoleGroup;
import com.apabi.flow.admin.service.AuthRoleGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthRoleGroupServiceImpl implements AuthRoleGroupService {

    @Autowired
    private AuthRoleGroupDao authRoleGroupDao;

    @Override
    public int deleteByGroupId(Integer id){
        return authRoleGroupDao.deleteByGroupId(id);
    }

    @Override
    public int save(AuthRoleGroup authRoleGroup){
        return authRoleGroupDao.save(authRoleGroup);
    }

    @Override
    public int getCountByRoleId(Integer roleId){
        return authRoleGroupDao.getCountByRoleId(roleId);
    }

    @Override
    public int getCountByGroupId(Integer groupId){
        return authRoleGroupDao.getCountByGroupId(groupId);
    }

    @Override
    public List<AuthRoleGroup> getGroupRoleCode(){
        return authRoleGroupDao.getGroupRoleCode();
    }
}
