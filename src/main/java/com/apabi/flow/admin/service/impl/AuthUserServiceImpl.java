package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.model.AuthGroup;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.admin.dao.AuthUserDao;
import com.apabi.flow.admin.service.AuthUserService;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * Created by liuyutong on 2018/1/18.
 */
@Service
public class AuthUserServiceImpl implements AuthUserService {

    private Logger log = LoggerFactory.getLogger(AuthUserServiceImpl.class);


    @Autowired
    private AuthUserDao userDao;


    @Override
    public AuthUser getCurrUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDao.findByUserName(userDetails.getUsername());
    }


    @Override
    public Page<AuthUser> findByPage(PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        return userDao.findByPage(pageInfo.getFilters());
    }


    @Override
    public Integer getCountByUserName(String userName) {
        return userDao.getCountByUserName(userName);
    }

    @Override
    public int save(AuthUser authUser) {
        return userDao.save(authUser);
    }


    @Override
    public List<AuthRes> getUserResByUserName(String name) {
        return userDao.getUserResByUserName(name);
    }

    @Override
    public int getUserIdByUserName(String name) {
        return userDao.getUserIdByUserName(name);
    }

    /**
     * 通过用户名获取用户Id，返回 Integer
     *
     * @param name
     */
    @Override
    public Integer findUserIdByUserName(String name) {
        return userDao.findUserIdByUserName(name);
    }

    @Override
    public AuthUser getById(Integer id) {
        return userDao.getById(id);
    }

    @Override
    public int update(AuthUser authUser)  {
        return userDao.update(authUser);
    }

    @Override
    public List<AuthRole> getUserRoleById(Integer id)  {
        return userDao.getUserRoleById(id);
    }

    @Override
    public int getCountByCrtId(Integer id)  {
        return userDao.getCountByCrtId(id);
    }

    @Override
    public int batchDelete(List<Integer> ids)  {
        return userDao.batchDelete(ids);
    }

    @Override
    public List<AuthRes> getUserResById(Integer id) {
        return userDao.getUserResById(id);
    }

    @Override
    public List<AuthGroup> getGroupByUserId(Integer id)  {
        return userDao.getGroupByUserId(id);
    }

    @Override
    public List<AuthRes> getUserModuleResByUserName(String name)  {
        return userDao.getUserModuleResByUserName(name);
    }

}
