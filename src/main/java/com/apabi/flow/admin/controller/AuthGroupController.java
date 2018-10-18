package com.apabi.flow.admin.controller;

import com.apabi.flow.admin.model.*;
import com.apabi.flow.admin.service.*;
import com.apabi.flow.admin.util.SecuritySessionInvalidate;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/admin/auth")
public class AuthGroupController {

    private Logger log = LoggerFactory.getLogger(AuthUserController.class);

    private AuthGroup authGroup;

    @Autowired
    private OrgService orgService;

    @Autowired
    private AuthGroupService authGroupService;

    @Autowired
    private AuthUserGroupService authUserGroupService;

    @Autowired
    private SecuritySessionInvalidate securitySessionInvalidate;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthRoleGroupService authRoleGroupService;

    private List<java.lang.Integer> authGroupIds;

    //用户组信息展示页面
    @RequestMapping(value = "/authgroup/group")
    public String group(Model model) {
        return "group/group";
    }

    //用户json数据
    @RequestMapping(value = "/authgroup/groupdata")
    @ResponseBody
    public PageInfo user(PageInfo pageInfo, String keywords) {
        try {
            long start = System.currentTimeMillis();
            pageInfo.getFilters().put("keywords", keywords);
            Page<AuthGroup> authGroups = authGroupService.findByPage(pageInfo);
            pageInfo = new PageInfo<>(authGroups);
            long end = System.currentTimeMillis();
            log.info("用户信息列表查询耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return pageInfo;
    }

    //用户组详情页面
    @RequestMapping(value = "/authgroup/groupdetail")
    public String groupdetail(Integer groupid,Model model) {
        try {
            //根据groupid查询用户信息
            authGroup = authGroupService.getById(groupid);
            model.addAttribute("ag", authGroup);
            //根据机构id查询机构名称
            Integer orgid = authGroup.getOrgId();
            String orgname = orgService.getById(orgid).getOrgName();
            model.addAttribute("orgname", orgname);
            //根据groupid查询角色具有的res（3类）
            List<AuthRes> list = authGroupService.getGroupResById(groupid);
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "group/groupdetail";
    }

    //增加用户组页面
    @RequestMapping(value = "/authgroup/groupadd")
    public String groupadd(Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        return "group/groupadd";
    }

    //保存用户组信息
    @RequestMapping(value="/authgroup/groupsave", method = RequestMethod.POST)
    @ResponseBody
    public String groupsave(AuthGroup authGroup) throws Exception{
        if(authGroup != null) {
            Date date = new Date();
            authGroup.setCrtDate(date);
            authGroup.setModifyDate(null);
            authGroup.setModifyUserId(null);
            authGroupService.save(authGroup);
        }
        return "success";
    }

    //编辑用户组界面
    @RequestMapping(value = "/authgroup/groupedit")
    public String groupedit(Integer groupid,Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        //根据groupid查询用户组信息
        authGroup = authGroupService.getById(groupid);
        model.addAttribute("ag", authGroup);
        return "group/groupedit";
    }

    //更新用户组信息
    @RequestMapping(value="/authgroup/groupupdate", method = RequestMethod.POST)
    @ResponseBody
    public String groupupdate(AuthGroup authGroup) throws Exception{
        if(authGroup != null) {
            Date date = new Date();
            authGroup.setModifyDate(date);
            authGroupService.update(authGroup);
        }
        return "success";
    }

    //用户组用户页面
    @RequestMapping(value = "/authgroup/groupuser")
    public String groupuser(Integer groupid,Model model) {
        try {
            //根据用户组Id查询用户信息
            List<AuthUser> listthis = authGroupService.getGroupUserById(groupid);
            model.addAttribute("listthis", listthis);
            //查询剩余用户信息
            List<AuthUser> listremain = authGroupService.getRemainUserById(groupid);
            model.addAttribute("listremain", listremain);
            model.addAttribute("groupid", groupid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "group/groupuser";
    }

    //用户组用户更新
    @RequestMapping(value="/authgroup/groupuserupdate", method = RequestMethod.POST)
    @ResponseBody
    public String groupuserupdate(Integer groupid,String userid) throws Exception{
        //点击提交后先删除后增加
        //List<AuthUserGroup> list = authUserGroupService.getUserIdByGroupId(groupid);
		/*for( int i = 0 ; i < list.size() ; i++) {
			authUserGroupService.deleteByUserId(list.get(i).getUserId());
		}*/
        //根据groupid删除用户
        authUserGroupService.deleteByGroupId(groupid);
        Date date = new Date();
        AuthUserGroup authUserGroup = new AuthUserGroup();
        if(userid.length() > 0){
            List<String> result = Arrays.asList(userid.split(","));
            List<Integer> intuserid = new ArrayList<Integer>();
            for(int i = 0; i < result.size(); i++){
                intuserid.add(Integer.parseInt(result.get(i)));
            }
            for(int i = 0; i < intuserid.size(); i++){
                authUserGroup.setCrtDate(date);
                authUserGroup.setCrtUserId(null);
                authUserGroup.setGroupId(groupid);
                authUserGroup.setUserId(intuserid.get(i));
                authUserGroupService.save(authUserGroup);
            }
        }
        //重新登录使用户session失效
        List<AuthUser> loginNameLists = authGroupService.getUserNameByGroupId(groupid);
        securitySessionInvalidate.removeSession(loginNameLists);
        return "success";
    }

    //用户组角色页面
    @RequestMapping(value = "/authgroup/grouprole")
    public String grouprole(Integer groupid,Model model) {
        try {
            //根据用户组Id查询角色信息
            List<AuthUser> listthis = authGroupService.getGroupRoleById(groupid);
            model.addAttribute("listthis", listthis);
            //查询剩余角色信息
            List<AuthUser> listremain = authGroupService.getRemainRoleById(groupid);
            model.addAttribute("listremain", listremain);
            model.addAttribute("groupid", groupid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "group/grouprole";
    }

    //用户组角色更新
    @RequestMapping(value="/authgroup/grouproleupdate", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public String grouproleupdate(Integer groupid,String roleid) throws Exception{
        //点击提交后先删除后增加
		/*List<AuthRoleGroup> list = authRoleGroupService.getRoleIdByGroupId(groupid);
		for( int i = 0 ; i < list.size() ; i++) {
			authRoleGroupService.deleteByRoleId(list.get(i).getRoleId());
			//删除时需要用户重新登录和更新Map
			//根据roleid查询rolecode
			AuthRole authRole = authRoleService.getById(list.get(i).getRoleId());
			Map<Integer, String> groupRoleMap = new HashMap<Integer, String>();
			groupRoleMap.put(groupid, authRole.getRoleCode());
			initConfigAttribute.removeGroupRoleMapCache(groupRoleMap);
		}*/
        //List<AuthRoleGroup> list = authRoleGroupService.getRoleIdByGroupId(groupid);
//        Map<String, Object> mp = redisService.hget(InitConfigAttribute.RGOUP_ROLE_MAP_CACHE_REDIS_KEY);
//        mp.remove(Integer.toString(groupid));
//        redisService.hset(InitConfigAttribute.RGOUP_ROLE_MAP_CACHE_REDIS_KEY, mp);
        //Map<String, Object> mpa = redisService.hget(RGOUP_ROLE_MAP_CACHE_REDIS_KEY);
		/*for(int i = 0 ; i < list.size() ; i++){
			Collection<String> listRole = (Collection<String>) mp.get(groupid);
			String groupids = Integer.toString(groupid);
			redisService.hsetHashVal(RGOUP_ROLE_MAP_CACHE_REDIS_KEY, groupids, listRole);
		}*/
		/*for( int i = 0 ; i < list.size() ; i++) {
			//删除时需要用户重新登录和更新Map
			//根据roleid查询rolecode
			AuthRole authRole = authRoleService.getById(list.get(i).getRoleId());
			Map<Integer, String> groupRoleMap = new HashMap<Integer, String>();
			groupRoleMap.put(groupid, authRole.getRoleCode());
			initConfigAttribute.removeGroupRoleMapCache(groupRoleMap);
		}*/
        //根据groupid删除
        authRoleGroupService.deleteByGroupId(groupid);
        Date date = new Date();
        AuthRoleGroup authRoleGroup = new AuthRoleGroup();
        if(roleid.length() > 0){
            List<String> result = Arrays.asList(roleid.split(","));
            List<Integer> introleid = new ArrayList<Integer>();
            for(int i = 0; i < result.size(); i++){
                introleid.add(Integer.parseInt(result.get(i)));
            }
            Collection<GrantedAuthority> listRole = new HashSet<GrantedAuthority>();

            for(int i = 0; i < introleid.size(); i++){
                authRoleGroup.setCrtDate(date);
                authRoleGroup.setCrtUserId(null);
                authRoleGroup.setGroupId(groupid);
                authRoleGroup.setRoleId(introleid.get(i));
                authRoleGroupService.save(authRoleGroup);

                AuthRole authRole = authRoleService.getById(introleid.get(i));
                listRole.add(new SimpleGrantedAuthority(authRole.getRoleCode()));

                //更新时需要用户重新登录和更新Map
                //根据roleid查询rolecode
				/*AuthRole authRole = authRoleService.getById(introleid.get(i));
				Map<Integer, String> groupRoleMap = new HashMap<Integer, String>();
				groupRoleMap.put(groupid, authRole.getRoleCode());
				initConfigAttribute.putGroupRoleMapCache(groupRoleMap);*/
            }
        }
        //重新登录使用户session失效
        List<AuthUser> loginNameLists = authGroupService.getUserNameByGroupId(groupid);
        securitySessionInvalidate.removeSession(loginNameLists);
        return "success";
    }

    //批量删除用户组
    @RequestMapping(value = "/authgroup/groupdelete", method = RequestMethod.GET)
    @ResponseBody
    public String groupdelete(String KeyValue) throws Exception {
        String[] items = KeyValue.split(",");
        List<String> groupList = new ArrayList<String>();
        Collections.addAll(groupList, items);
        authGroupIds = new ArrayList<Integer>();
        //将String List转换成Integer List
        for(String str : groupList) {
            int i = Integer.parseInt(str);
            authGroupIds.add(i);
        }
        //删除之前需要先检查关联性
        int count = 0;
        for(int i = 0; i < authGroupIds.size(); i++){
            int countref1 = authUserGroupService.getCountByGroupId(authGroupIds.get(i));
            int countref2 = authRoleGroupService.getCountByGroupId(authGroupIds.get(i));
            count += countref1 + countref2;
        }
        if(count > 0){
            return "fail";
        }else{
            if(authGroupIds != null && authGroupIds.size() > 0){
                authGroupService.batchDelete(authGroupIds);
                //删除后需要用户重新登录并更新缓存
                //需要查找使用该用户组的用户
                for(int i = 0; i < authGroupIds.size(); i++){
                    List<AuthUser> loginNameLists = authGroupService.getGroupUserById(authGroupIds.get(i));
                    securitySessionInvalidate.removeSession(loginNameLists);
                }

            }
            return "success";
        }
    }

}
