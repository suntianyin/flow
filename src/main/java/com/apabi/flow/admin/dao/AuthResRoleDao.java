package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthResRole;

import java.util.List;

public interface AuthResRoleDao {

    int save(AuthResRole authResRole);

    int getCountByRoleId(Integer roleId);

    int getCountByResId(Integer resId);

    List<AuthResRole> getResIdByRoleId(Integer roleId);

    int deleteByRoleId(Integer roleId);
}
