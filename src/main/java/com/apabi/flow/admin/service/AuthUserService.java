package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created by liuyutong on 2018/1/18.
 */
public interface AuthUserService {
    /**
     * 获取当前登录用户
     * @return AuthUser
     */
    AuthUser getCurrUser();



    Page<AuthUser> findByPage(PageInfo pageInfo);


    /**
     * 重复性判断
     */
    Integer getCountByUserName(String userName);

    /**
     * 入库
     */
    int save(AuthUser authUser);


    /**
     * 通过用户名查询用户的权限
     */
    List<AuthRes> getUserResByUserName(String name);

    /**
     * 通过用户名获取用户Id
     */
    int getUserIdByUserName(String name) ;

    /**
     * 通过用户名获取用户Id，返回 Integer
     */
    Integer findUserIdByUserName(String name) ;

    /**
     * 根据ID查询
     */
    AuthUser getById(java.lang.Integer id);


    /**
     * 更新
     */
    int update(AuthUser authUser) ;

    /**
     * 通过用户ID查询用户的角色
     */
    List<AuthRole> getUserRoleById(Integer id) ;

    /**
     * 通过创建用户ID获取
     */
    int getCountByCrtId(Integer id) ;

    /**
     * 批量删除
     */
    int batchDelete(List<java.lang.Integer> ids) ;

    /**
     * 通过用户ID查询用户的权限
     */
    List<AuthRes> getUserResById(Integer id) ;

    /**
     * 通过用户ID查询用户组信息
     */
    List<AuthGroup> getGroupByUserId(Integer id);

    /**
     * 通过用户名查询用户的模块权限
     */
    List<AuthRes> getUserModuleResByUserName(String name) ;
}
