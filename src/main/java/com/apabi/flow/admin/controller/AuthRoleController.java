package com.apabi.flow.admin.controller;

import com.apabi.flow.admin.model.*;
import com.apabi.flow.admin.service.*;
import com.apabi.flow.admin.util.SecuritySessionInvalidate;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/auth")
public class AuthRoleController {
    @Autowired
    private OrgService orgService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthRoleGroupService authRoleGroupService;

    @Autowired
    private AuthResRoleService authResRoleService;

    @Autowired
    private SecuritySessionInvalidate securitySessionInvalidate;

    @Autowired
    private AuthResService authResService;

    private List<java.lang.Integer> authRoleIds;

    private AuthRole authRole;

    AuthRole role;

    private Logger log = LoggerFactory.getLogger(AuthRoleController.class);

    @RequestMapping(value = "/authrole/role")
    public String role(Model model) {
        return "role/role";
    }

    @RequestMapping(value = "/authrole/roledata")
    @ResponseBody
    public PageInfo user(PageInfo pageInfo, String keywords) {
        try {
            long start = System.currentTimeMillis();
            pageInfo.getFilters().put("keywords", keywords);
            Page<AuthRole> authRoles = authRoleService.findByPage(pageInfo);
            pageInfo = new PageInfo<>(authRoles);
            long end = System.currentTimeMillis();
            log.info("用户信息列表查询耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return pageInfo;
    }

    //角色详情页面
    @RequestMapping(value = "/authrole/roledetail")
    public String roledetail(Integer roleid,Model model) {
        try {
            //根据roleid查询角色信息
            authRole = authRoleService.getById(roleid);
            model.addAttribute("ar", authRole);
            //根据机构id查询机构名称
            Integer orgid = authRole.getOrgId();
            String orgname = orgService.getById(orgid).getOrgName();
            model.addAttribute("orgname", orgname);
            //根据roleid查询角色具有的res（3类）
            List<AuthRes> list = authRoleService.getRoleResById(roleid);
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "role/roledetail";
    }

    //增加角色页面
    @RequestMapping(value = "/authrole/roleadd")
    public String roleadd(Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        return "role/roleadd";
    }

    //保存角色信息
    @RequestMapping(value="/authrole/rolesave", method = RequestMethod.POST)
    @ResponseBody
    public String rolesave(AuthRole authRole) throws Exception{
        if(authRole != null) {
            Date date = new Date();
            authRole.setCrtDate(date);
            authRole.setModifyDate(null);
            authRole.setModifyUserId(null);
            authRoleService.save(authRole);
        }
        return "success";
    }

    //编辑角色界面
    @RequestMapping(value = "/authrole/roleedit")
    public String roleedit(Integer roleid,Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        //根据userid查询用户信息
        authRole = authRoleService.getById(roleid);
        model.addAttribute("ar", authRole);
        return "role/roleedit";
    }

    //更新角色信息
    @RequestMapping(value="/authrole/roleupdate", method = RequestMethod.POST)
    @ResponseBody
    public String roleupdate(AuthRole authRole) throws Exception{
        if(authRole != null) {
            Date date = new Date();
            authRole.setModifyDate(date);
            authRoleService.update(authRole);
        }
        return "success";
    }

    //检查是否存在重复项
    @RequestMapping(value="/authrole/fieldexist", method = RequestMethod.GET)
    @ResponseBody
    public Integer FieldExist(String keyfield,String fieldvalue) throws Exception{
        //检查RoleCode是否重复
        if(keyfield.equals("RoleCode")){
            String rolecode = fieldvalue;
            Integer count = authRoleService.getCountByRoleCode(rolecode);
            if(count > 0){
                return 1;
            }
        }
        return 0;
    }

    //角色配置权限页面
    @RequestMapping(value = "/authrole/roleres")
    public String roleres(Integer roleid,Model model) {
        try {
            //根据角色Id查询资源信息
            List<AuthRes> listthis = authRoleService.getRoleResById(roleid);
            model.addAttribute("listthis", listthis);
            //查询剩余资源信息
            List<AuthRes> listremain = authRoleService.getRemainResById(roleid);
            model.addAttribute("listremain", listremain);
            model.addAttribute("roleid", roleid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "role/roleres";
    }

    //角色配置权限测试页面
    @RequestMapping(value = "/authrole/rolerestest")
    public String rolerestest(Integer roleid,Model model) {
        try {
            String array = authRoleService.getTreesByRoleId(roleid);
            model.addAttribute("json", array);
            model.addAttribute("roleid", roleid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "role/rolerestest";
    }

    //批量删除角色
    @RequestMapping(value = "/authrole/roledelete", method = RequestMethod.GET)
    @ResponseBody
    public String roledelete(String KeyValue) throws Exception {
        String[] items = KeyValue.split(",");
        List<String> roleList = new ArrayList<String>();
        Collections.addAll(roleList, items);
        authRoleIds = new ArrayList<Integer>();
        //将String List转换成Integer List
        for(String str : roleList) {
            int i = Integer.parseInt(str);
            authRoleIds.add(i);
        }
        //删除之前需要先检查关联性
        int count = 0;
        for(int i = 0; i < authRoleIds.size(); i++){
            int countref1 = authRoleGroupService.getCountByRoleId(authRoleIds.get(i));
            int countref2 = authResRoleService.getCountByRoleId(authRoleIds.get(i));
            count += countref1 + countref2;
        }
        if(count > 0){
            return "fail";
        }else{
            if(authRoleIds != null && authRoleIds.size() > 0){
                authRoleService.batchDelete(authRoleIds);
            }
            return "success";
        }
    }

    //角色配置权限更新
    @RequestMapping(value="/authrole/roleresupdate", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(propagation= Propagation.REQUIRED)
    public String roleresupdate(Integer roleid, String resid) throws Exception{
        //点击提交后先删除后增加
        //List<AuthResRole> list = authResRoleService.getResIdByRoleId(roleid);
        List<AuthResRole> list = authResRoleService.getResIdByRoleId(roleid);
//        Map<String, Object> mp = redisService.hget(InitConfigAttribute.RES_ROLE_MAP_CACHE_REDIS_KEY);
        for(int i = 0; i < list.size(); i++){
            AuthRes authRes = authResService.getById(list.get(i).getResId());
            AuthRole authRole = authRoleService.getById(roleid);
        }
			//删除时需要用户重新登录和更新Map
			//根据resId查找resUrl
			//根据roleid查询rolecode

        authResRoleService.deleteByRoleId(roleid);
        //删除时需要用户重新登录和更新Map
        //根据resId查找resUrl
        //根据roleid查询rolecode
        Date date = new Date();
        AuthResRole authResRole = new AuthResRole();
        if(resid.length() > 0){
            List<String> result = Arrays.asList(resid.split(","));
            List<Integer> intresid = new ArrayList<Integer>();
            for(int i = 0; i < result.size(); i++){
                intresid.add(Integer.parseInt(result.get(i)));
            }
            for(int i = 0; i < intresid.size(); i++){
                authResRole.setCrtDate(date);
                authResRole.setCrtUserId(null);
                authResRole.setRoleId(roleid);
                authResRole.setResId(intresid.get(i));
                authResRoleService.save(authResRole);
                AuthRes authRes = authResService.getById(intresid.get(i));
                AuthRole authRole = authRoleService.getById(roleid);
//                Collection<ConfigAttribute> listRole = (Collection<ConfigAttribute>) mp.get(authRes.getUrl());
                SecurityConfig securityConfig = new SecurityConfig(authRole.getRoleCode());

            }
        }
        //重新登录使用户session失效
        List<AuthUser> loginNameLists = authRoleService.getUserNameByRoleId(roleid);
        securitySessionInvalidate.removeSession(loginNameLists);
        return "success";
    }

}
