package com.apabi.flow.admin.controller;

import com.apabi.flow.admin.model.*;
import com.apabi.flow.admin.service.AuthUserGroupService;
import com.apabi.flow.admin.service.AuthUserService;
import com.apabi.flow.admin.service.OrgService;
import com.apabi.flow.admin.util.MD5Util;
import com.apabi.flow.common.PageInfo;
import com.apabi.flow.common.model.ResponseData;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/auth")
public class AuthUserController {

    AuthUser user;

    public static String sdf = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private OrgService orgService;
    private AuthUser authUser;
    @Autowired
    private AuthUserGroupService authUserGroupService;

    private List<java.lang.Integer> authUserIds;

    private Logger log = LoggerFactory.getLogger(AuthUserController.class);



    //用户信息展示页面
    @RequestMapping(value = "/authuser/user")
    public String user(Model model) {
        try {
            //载入页面之前判断用户具有的权限
            //Map<String, Collection<ConfigAttribute>> map = initConfigAttribute.getResRoleMapCache();
            //获取当前用户登录的对象

            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<AuthRes> list = authUserService.getUserResByUserName(userDetails.getUsername());
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/user";
    }

    @RequestMapping(value = "/authuser/userdata")
    @ResponseBody
    public PageInfo user(PageInfo pageInfo, String keywords) {
        try {
            long start = System.currentTimeMillis();
            pageInfo.getFilters().put("keywords", keywords);
            Page<AuthUser> authUsers = authUserService.findByPage(pageInfo);
            pageInfo = new PageInfo<>(authUsers);
            long end = System.currentTimeMillis();
            log.info("用户信息列表查询耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return pageInfo;
    }

    //用户详情页面
    @RequestMapping(value = "/authuser/userdetail")
    public String userdetail(Integer userid,Model model) {
        try {
            //根据userid查询用户信息
            authUser = authUserService.getById(userid);
            model.addAttribute("au", authUser);
            //根据机构id查询机构名称
            Integer orgid = authUser.getOrgId();
            String orgname = orgService.getById(orgid).getOrgName();
            model.addAttribute("orgname", orgname);
            //查询创建用户名
            AuthUser crtUser = authUserService.getById(authUser.getCrtUserId());
            String crtUserName = crtUser.getUserName();
            model.addAttribute("crtUserName", crtUserName);
            //查询修改用户名
            //AuthUser modifyUser = authUserService.getById(authUser.getModifyUserId());
            //String modifyUserName = modifyUser.getUserName();
            //model.addAttribute("modifyUserName", modifyUserName);
            //根据userid查询用户具有的res（3类），通过UserId查GroupId再查RoleId再查Res，获取用户权限
            List<AuthRes> list = authUserService.getUserResById(userid);
            model.addAttribute("list", list);
            //查询用户所在用户组
            List<AuthGroup> listgroup = authUserService.getGroupByUserId(userid);
            model.addAttribute("listgroup", listgroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/userdetail";
    }

    //增加用户页面
    @RequestMapping(value = "/authuser/useradd")
    public String useradd(Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        return "user/useradd";
    }

    //编辑用户界面
    @RequestMapping(value = "/authuser/useredit")
    public String useredit(Integer userid,Model model) throws Exception {
        //查找所有的机构Id
        List<Org> list = orgService.queryAll();
        model.addAttribute("list", list);
        //根据userid查询用户信息
        authUser = authUserService.getById(userid);
        AuthUser authUserTemp = authUserService.getById(authUser.getCrtUserId());
        String crtUserName = authUserTemp.getUserName();
        model.addAttribute("au", authUser);
        model.addAttribute("crtUserName", crtUserName);
        return "user/useredit";
    }

    //保存用户信息
    @RequestMapping(value="/authuser/usersave", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData usersave(AuthUser authUser, String captcha){
        ResponseData responseData = new ResponseData();
        try {
            if(authUser != null) {
                //判断用户名是否存在
                int cnt = authUserService.getCountByUserName(authUser.getUserName());
                if (cnt>0){
                    return responseData.apply(0, "用户名已存在，请重新输入！");
                }

//                String email = authUser.getEmail();
//                if (StringUtils.isNotBlank(email) && !authUserService.checkEmailCatpcha(email, captcha)) {
//                    return responseData.apply(0, "验证码错误！");
//                }
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                //通过用户名获取用户Id
                int userId = authUserService.getUserIdByUserName(userDetails.getUsername());
                authUser.setCrtUserId(userId);
                Date date = new Date();
//                authUser.setId((int) date.getTime());
                authUser.setCrtDate(date);
                //将明文转为密文存储
                authUser.setPassword(MD5Util.encode(authUser.getPassword()));
                authUser.setModifyDate(null);
                authUser.setModifyPasswordDate(null);
                authUser.setModifyUserId(null);
                authUserService.save(authUser);
                //重新初始化配置
            }
        }catch (Exception e) {
            log.error("Exception: {}", e);
            return responseData.apply(0, "发生异常！请稍后重试！");
        }
        return responseData.apply(1, "添加成功！");
    }

    //更新用户信息
    @RequestMapping(value="/authuser/userupdate", method = RequestMethod.POST)
    @ResponseBody
    public String userupdate(AuthUser authUser) throws Exception{
        if(authUser != null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            authUser.setModifyDate(date);
            //通过用户名获取用户Id
            int userId = authUserService.getUserIdByUserName(userDetails.getUsername());
            int crtUserId = authUser.getCrtUserId();
            if(crtUserId == userId ){
                authUser.setModifyUserId(userId);
                authUserService.update(authUser);
                return "success";
            }else{
                return "false";
            }
        }
        return "success";
    }

    //检查是否存在重复项
    @RequestMapping(value="/authuser/fieldexist", method = RequestMethod.GET)
    @ResponseBody
    public Integer FieldExist(String keyfield,String fieldvalue) throws Exception{
        //检查用户名是否重复
        if(keyfield.equals("UserName")){
            String username = fieldvalue;
            Integer count = authUserService.getCountByUserName(username);
            if(count > 0){
                return 1;
            }
        }
        return 0;
    }

    //用户角色页面
    @RequestMapping(value = "/authuser/userrole")
    public String userrole(Integer userid,Model model) {
        try {
            //根据用户Id查询角色信息
            List<AuthRole> listthis = authUserService.getUserRoleById(userid);
            model.addAttribute("listthis", listthis);
            //查询所有角色信息
            //List<AuthRole> list = authRoleService.queryAll();
            //model.addAttribute("list", list);
            //去掉重复的角色信息
            //List<AuthUser> listremain = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/userrole";
    }

    //批量删除用户
    @RequestMapping(value = "/authuser/userdelete", method = RequestMethod.GET)
    @ResponseBody
    public String userdelete(String KeyValue) throws Exception {
        String[] items = KeyValue.split(",");
        List<String> userList = new ArrayList<String>();
        Collections.addAll(userList, items);
        authUserIds = new ArrayList<Integer>();
        //将String List转换成Integer List
        for(String str : userList) {
            int i = Integer.parseInt(str);
            authUserIds.add(i);
        }
        //删除之前需要检查关联性
        int count = 0;
        int crt_count = 0;
        for(int i = 0; i < authUserIds.size(); i++){
            int countref = authUserGroupService.getCountByUserId(authUserIds.get(i));
            count += countref;
        }
        for(int i = 0; i < authUserIds.size(); i++){
            int countref = authUserService.getCountByCrtId(authUserIds.get(i));
            crt_count += countref;
        }
        if(count > 0 || crt_count > 0){
            return "fail";
        }else{
            if(authUserIds != null && authUserIds.size() > 0){
                authUserService.batchDelete(authUserIds);
            }
            return "success";
        }
    }

    //用户修改密码界面
    @RequestMapping(value = "/authuser/userrepass")
    public String userrepass(Integer userid,Model model) throws Exception {
        //根据userid查询用户信息
        authUser = authUserService.getById(userid);
        model.addAttribute("au", authUser);
        return "user/userrepass";
    }

    //更新用户密码
    @RequestMapping(value="/authuser/updatepassword", method = RequestMethod.POST)
    @ResponseBody
    public String updatepassword(AuthUser authUser) throws Exception{
        if(authUser != null) {
            authUser.setPassword(MD5Util.encode(authUser.getPassword()));
            authUserService.update(authUser);
        }
        return "success";
    }
}
