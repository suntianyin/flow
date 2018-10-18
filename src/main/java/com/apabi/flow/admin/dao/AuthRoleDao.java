package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuthRoleDao {

    /**
     * 分页查询
     */
    Page<AuthRole> findByPage(Map filters);

    /**
     *入库
     */
    int save(AuthRole authRole);
    /**
     * 根据id查询
     */
    AuthRole getById(java.lang.Integer id);

    /**
     *更新数据
     */
    int update(AuthRole authRole);


    List<AuthRes> getRoleResById(Integer id) ;

    List<AuthRes> getRemainResById(Integer id);

    int batchDelete(@Param("ids") List<Integer> ids);

    List<AuthUser> getUserNameByRoleId(Integer id);

    Integer getCountByRoleCode(String rolecode);

}
