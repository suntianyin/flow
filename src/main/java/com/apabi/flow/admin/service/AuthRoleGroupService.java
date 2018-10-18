package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthRoleGroup;

import java.util.List;

public interface AuthRoleGroupService {

    /**
     * 根据GroupId删除
     */
    int deleteByGroupId(java.lang.Integer id);

    /**
     * 入库
     */
    int save(AuthRoleGroup authRoleGroup);

    /**
     * 删除之前需要先检查关联性
     */
    int getCountByRoleId(Integer roleId);

    int getCountByGroupId(Integer groupId);

    /**
     * 查询用户组、角色对应关系
     */
    List<AuthRoleGroup> getGroupRoleCode();
}
