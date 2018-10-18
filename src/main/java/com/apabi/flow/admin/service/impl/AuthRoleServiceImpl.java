package com.apabi.flow.admin.service.impl;

import com.apabi.flow.admin.dao.AuthResDao;
import com.apabi.flow.admin.dao.AuthRoleDao;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthRole;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.admin.service.AuthRoleService;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthRoleServiceImpl implements AuthRoleService {

    @Autowired
    private AuthRoleDao roleDao;
    @Autowired
    private AuthResDao resDao;

    @Override
    public Page<AuthRole> findByPage(PageInfo pageInfo){
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        return roleDao.findByPage(pageInfo.getFilters());
    }

    @Override
    public int save(AuthRole authRole){
        return roleDao.save(authRole);
    }

    @Override
    public AuthRole getById(Integer id){
        return roleDao.getById(id);
    }

    @Override
    public int update(AuthRole authRole){
        return roleDao.update(authRole);
    }

    @Override
    public List<AuthRes> getRoleResById(Integer id){
        return roleDao.getRoleResById(id);
    }

    @Override
    public List<AuthRes> getRemainResById(Integer id){
        return roleDao.getRemainResById(id);
    }

    @Override
    public String getTreesByRoleId(Integer id){
        List<AuthRes> listAllReses = resDao.findAll();
        //查找该角色下对应的资源
        List<AuthRes> listThisReses = roleDao.getRoleResById(id);
        return generateJsonArray(listAllReses,listThisReses).toString();
    }

    @Override
    public int batchDelete(List<Integer> ids){
        return roleDao.batchDelete(ids);
    }

    @Override
    public List<AuthUser> getUserNameByRoleId(Integer id){
        return roleDao.getUserNameByRoleId(id);
    }

    @Override
    public Integer getCountByRoleCode(String rolecode) {
        return roleDao.getCountByRoleCode(rolecode);
    }

    private JSONArray generateJsonArray(List<AuthRes> listAllReses, List<AuthRes> listThisReses){
        JSONArray jsonArray = new JSONArray();
        for(AuthRes authRes : listAllReses){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", authRes.getId());
            if(authRes.getType() == 1 || authRes.getType() == 2){
                jsonObject.put("pId", null);
            }else{
                jsonObject.put("pId", authRes.getParentId());
            }
            jsonObject.put("name", authRes.getName());
            jsonObject.put("title", authRes.getUrl());
            jsonObject.put("type", authRes.getType());
            if(listThisReses.contains(authRes)){
                jsonObject.put("checked", true);
                jsonObject.put("open", true);
            }
            if(authRes.getType() == 1){
                jsonObject.put("isParent", true);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
