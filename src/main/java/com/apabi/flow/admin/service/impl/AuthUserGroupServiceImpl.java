package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthUserGroupDao;
import com.apabi.flow.admin.model.AuthUserGroup;
import com.apabi.flow.admin.service.AuthUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthUserGroupServiceImpl implements AuthUserGroupService {
    @Autowired
    private AuthUserGroupDao authUserGroupDao;

    @Override
    public int deleteByGroupId(Integer id){
        return authUserGroupDao.deleteByGroupId(id);
    }

    @Override
    public int save(AuthUserGroup authUserGroup){
        return authUserGroupDao.save(authUserGroup);
    }

    @Override
    public int getCountByUserId(Integer userId){
        return authUserGroupDao.getCountByUserId(userId);
    }

    @Override
    public int getCountByGroupId(Integer groupId) {
        return authUserGroupDao.getCountByGroupId(groupId);
    }
}
