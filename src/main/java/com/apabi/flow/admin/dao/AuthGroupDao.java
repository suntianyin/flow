package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthUser;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuthGroupDao {
    /**
     * 分页查询
     */
    Page<AuthGroup> findByPage(Map<String,Object> filters);

    int save(AuthGroup authGroup);

    /**
     * 根据id查询
     */
    AuthGroup getById(Integer id);

    /**
     * 更新
     */
    int update(AuthGroup authGroup);

    List<AuthUser> getGroupUserById(Integer id);

    List<AuthUser> getRemainUserById(Integer id);

    /**
     * 根据groupid查询哪些用户在这个组里面
     */
    List<AuthUser> getUserNameByGroupId(Integer id);
    /**
     * 根据用户组Id查询角色信息
     */
    List<AuthUser> getGroupRoleById(Integer id);

    /**
     * 查询剩余角色信息
     */
    List<AuthUser> getRemainRoleById(Integer id);

    int batchDelete(@Param("ids") List<Integer> ids);

    List<AuthRes> getGroupResById(Integer id);
}
