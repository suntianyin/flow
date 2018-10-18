package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.AuthRes;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuthResDao {

    List<AuthRes> findAll();

    List<AuthRes> findByUserId(Integer userId);

    /**
     * 分页查询
     */
    Page<AuthRes> findByPage(Map<String,Object> filters);

    /**
     * 入库
     */
    int save(AuthRes authRes);

    /**
     * 根据id获取数据
     */
    AuthRes getById(Integer id);

    /**
     *跟新
     */
    int update(AuthRes authRes);

    int batchDelete(@Param("ids") List<Integer> ids);

    List<AuthRes> getUrlRole();

    int getCountByMenuId(Integer menuId);

}
