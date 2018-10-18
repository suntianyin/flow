package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;


public interface AuthUserDao {
//    @Cacheable("user")
    AuthUser findByUserName(String username);
    /**
     * 重复性判断
     */
    Integer getCountByUserName(String userName);

    /**
     * 入库
     */
    int save(AuthUser authUser) ;
    /**
     * 更新
     */
    int update(AuthUser authUser) ;
    /**
     * 分页查询
     */
    Page<AuthUser>  findByPage(Map<String,Object> filters) ;
    /**
     * 根据id查询
     */
    AuthUser getByUserId(Integer id);

    List<AuthRes> getUserResByUserName(String name);

    /**
     *通过用户名获取用户id
     */
    int getUserIdByUserName(String name);

    AuthUser getById(Integer id);

    List<AuthRole> getUserRoleById(Integer id);

    int getCountByCrtId(Integer id);

    int batchDelete(@Param("ids") List<Integer> ids);

    Page<AuthUser> getByName(Integer pageNo, Integer pageSize, String name);

    List<AuthRes> getUserResById(Integer id);

    List<AuthGroup> getGroupByUserId(Integer id);

    List<AuthRes> getUserModuleResByUserName(String name);

    Integer findUserIdByUserName(String name);
}
