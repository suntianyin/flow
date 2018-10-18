package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AuthResService {

    /**
     * 加载权限表中所有权限
     */
    Map<String, Collection<ConfigAttribute>> loadResourceDefine();

    /**
     * 分页查询
     */
    Page<AuthRes> findByPage(PageInfo pageInfo) ;

    /**
     * 入库
     */
    int save(AuthRes authRes);

    /**
     * 根据ID查询
     */
    AuthRes getById(Integer id);

    /**
     * 更新
     */
    int update(AuthRes authRes);

    /**
     * 批量删除
     */
    int batchDelete(List<Integer> ids);

    /**
     * 全部查询
     */
    List<AuthRes> queryAll();

    /**
     * 查询链接角色对应关系
     */
    List<AuthRes> getUrlRole();

    /**
     * 删除之前需要先检查关联性
     */
    int getCountByMenuId(Integer menuId);
}
