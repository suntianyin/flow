package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthGroupDao;
import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.admin.service.AuthGroupService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.apabi.flow.common.PageInfo;

import java.util.List;

@Service
public class AuthGroupServiceImpl implements AuthGroupService {
    @Autowired
    private AuthGroupDao authGroupDao;

    @Override
    public Page<AuthGroup> findByPage(PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPage(),pageInfo.getPageSize());
        return authGroupDao.findByPage(pageInfo.getFilters());
    }

    @Override
    public int save(AuthGroup authGroup) {
        return authGroupDao.save(authGroup);
    }

    @Override
    public AuthGroup getById(Integer id)  {
        return authGroupDao.getById(id);
    }

    @Override
    public int update(AuthGroup authGroup)  {
        return authGroupDao.update(authGroup);
    }

    @Override
    public List<AuthUser> getGroupUserById(Integer id) {
        return authGroupDao.getGroupUserById(id);
    }

    @Override
    public List<AuthUser> getRemainUserById(Integer id)  {
        return authGroupDao.getRemainUserById(id);
    }

    @Override
    public List<AuthUser> getUserNameByGroupId(Integer id) {
        return authGroupDao.getUserNameByGroupId(id);
    }

    @Override
    public List<AuthUser> getGroupRoleById(Integer id)  {
        return authGroupDao.getGroupRoleById(id);
    }

    @Override
    public List<AuthUser> getRemainRoleById(Integer id) {
        return authGroupDao.getRemainRoleById(id);
    }

    @Override
    public int batchDelete(List<Integer> ids) {
        return authGroupDao.batchDelete(ids);
    }

    @Override
    public List<AuthRes> getGroupResById(Integer id) {
        return authGroupDao.getGroupResById(id);
    }
}
