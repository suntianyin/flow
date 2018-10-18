package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthUserGroup;

public interface AuthUserGroupService {

    /**
     * 根据GroupId删除
     */
    int deleteByGroupId(Integer id);

    /**
     * 入库
     */
    int save(AuthUserGroup authUserGroup) ;

    /**
     * 删除之前需要先检查关联性
     */
    int getCountByUserId(Integer userId);
    int getCountByGroupId(Integer groupId);
}
