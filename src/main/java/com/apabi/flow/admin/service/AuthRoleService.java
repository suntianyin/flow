package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;

import java.util.List;

public interface AuthRoleService {

    /**
     * 分页查询
     */
    Page<AuthRole> findByPage(PageInfo pageInfo) ;

    /**
     * 入库
     */
    int save(AuthRole authRole);

    /**
     * 根据ID查询
     */
    AuthRole getById(Integer id);

    /**
     * 更新
     */
    int update(AuthRole authRole);

    /**
     * 通过roleId查询角色具有的权限
     */
    List<AuthRes> getRoleResById(Integer id);

    /**
     * 通过roleId查询剩余的权限
     */
    List<AuthRes> getRemainResById(Integer id);

    /**
     * 通过roleId来构造角色树
     */
    String getTreesByRoleId(Integer id);

    /**
     * 批量删除
     */
    int batchDelete(List<java.lang.Integer> ids);

    /**
     * 通过roleId查询哪些用户具有该角色
     */
    List<AuthUser> getUserNameByRoleId(Integer id);

    /**
     * 检查RoleCode是否重复
     */
    Integer getCountByRoleCode(String rolecode);
}
