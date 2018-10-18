package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthUserGroup;

public interface AuthUserGroupDao {

    int deleteByGroupId(Integer id);
    int save(AuthUserGroup authUserGroup);
    int getCountByUserId(Integer userId);
    int getCountByGroupId(Integer groupId);
}
