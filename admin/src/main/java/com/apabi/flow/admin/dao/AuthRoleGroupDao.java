package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthRoleGroup;

import java.util.List;

public interface AuthRoleGroupDao {

    /**
     * 根据ID删除
     */
    int deleteByGroupId(java.lang.Integer id) ;

    int save(AuthRoleGroup authRoleGroup);

    int getCountByRoleId(Integer roleId);

    int getCountByGroupId(Integer groupId);

    List<AuthRoleGroup> getGroupRoleCode();
}
