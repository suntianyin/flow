package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthResDao;
import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.service.AuthResService;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class AuthResServiceImpl implements AuthResService {

    @Autowired
    private AuthResDao authResDao;

    /**
     * 加载权限表中所有权限
     */
    @Override
    @Cacheable("loadPermissions")
    public Map<String, Collection<ConfigAttribute>> loadResourceDefine(){
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        List<AuthRes> permissions = authResDao.findAll();
        for(AuthRes permission : permissions) {
            array = new ArrayList<>();
            cfg = new SecurityConfig(permission.getName());
            //此处只添加了用户的名字，其实还可以添加更多权限的信息，例如请求方法到ConfigAttribute的集合中去。此处添加的信息将会作为MyAccessDecisionManager类的decide的第三个参数。
            array.add(cfg);
            //用权限的getUrl() 作为map的key，用ConfigAttribute的集合作为 value，
            map.put(permission.getUrl(), array);
        }
        return map;
    }

    @Override
    public Page<AuthRes> findByPage(PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        return authResDao.findByPage(pageInfo.getFilters());
    }

    @Override
    public int save(AuthRes authRes) {
        return authResDao.save(authRes);
    }

    @Override
    public AuthRes getById(Integer id) {
        return authResDao.getById(id);
    }

    @Override
    public int update(AuthRes authRes) {
        return authResDao.update(authRes);
    }

    @Override
    public int batchDelete(List<Integer> ids) {
        return authResDao.batchDelete(ids);
    }

    @Override
    public List<AuthRes> queryAll() {
        return authResDao.findAll();
    }

    @Override
    public List<AuthRes> getUrlRole() {
        return authResDao.getUrlRole();
    }

    @Override
    public int getCountByMenuId(Integer menuId){
        return authResDao.getCountByMenuId(menuId);
    }

}
