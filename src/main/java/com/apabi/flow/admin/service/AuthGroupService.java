package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;


import java.util.List;

public interface AuthGroupService {

    /**
     * 分页查询
     */
    Page<AuthGroup> findByPage(PageInfo pageInfo) ;

    /**
     * 入库
     */
    int save(AuthGroup authGroup) ;

    /**
     * 根据ID查询
     */
    AuthGroup getById(Integer id) ;

    /**
     * 更新
     */
    int update(AuthGroup authGroup) ;

    /**
     * 查询用户组用户成员
     */
    List<AuthUser> getGroupUserById(Integer id) ;

    /**
     * 查询剩余用户成员
     */
    List<AuthUser> getRemainUserById(Integer id) ;

    /**
     * 根据groupid查询哪些用户在这个组里面
     */
    List<AuthUser> getUserNameByGroupId(Integer id) ;

    /**
     * 根据用户组Id查询角色信息
     */
    List<AuthUser> getGroupRoleById(Integer id) ;

    /**
     * 查询剩余角色信息
     */
    List<AuthUser> getRemainRoleById(Integer id);

    /**
     * 批量删除
     */
    int batchDelete(List<java.lang.Integer> ids);

    /**
     * 根据groupid查询角色具有的res
     */
    List<AuthRes> getGroupResById(Integer id);

}
