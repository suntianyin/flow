package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthResRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuthResRoleService {

    /**
     * 入库
     */
    int save(AuthResRole authResRole);

    /**
     * 删除之前需要先检查关联性
     */
    int getCountByRoleId(Integer roleId);

    int getCountByResId(Integer resId);

    /**
     * 根据roleId查询resId
     */
    List<AuthResRole> getResIdByRoleId(Integer roleId);

    /**
     * 根据roleId删除
     */
    int deleteByRoleId(Integer roleId);


}
