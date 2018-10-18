package com.apabi.flow.admin.controller;

import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.Menu;
import com.apabi.flow.admin.service.AuthResRoleService;
import com.apabi.flow.admin.service.AuthResService;
import com.apabi.flow.admin.service.AuthUserService;
import com.apabi.flow.admin.service.MenuService;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value="/admin/auth")
public class AuthResController {

    private Logger log = LoggerFactory.getLogger(AuthRoleController.class);

    private AuthRes authRes;

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthResService authResService;

    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private AuthResRoleService authResRoleService;

    private List<java.lang.Integer> authResIds;

    //资源展示页面
    @RequestMapping(value = "/authres/res")
    public String res(Model model) {
        return "res/res";
    }

    @RequestMapping(value = "/authres/resdata")
    @ResponseBody
    public PageInfo user(PageInfo pageInfo, String keywords) {
        try {
            long start = System.currentTimeMillis();
            pageInfo.getFilters().put("keywords", keywords);
            Page<AuthRes> authRes = authResService.findByPage(pageInfo);
            pageInfo = new PageInfo<>(authRes);
            long end = System.currentTimeMillis();
            log.info("用户信息列表查询耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return pageInfo;
    }

    //资源详情页面
    @RequestMapping(value = "/authres/resdetail")
    public String resdetail(Integer resid,Model model) {
        try {
            //根据resid查询资源信息
            authRes = authResService.getById(resid);
            model.addAttribute("ar", authRes);
            //根据菜单id查询菜单名称
            Integer menuid = authRes.getMenuId();
            String menuname = menuService.getById(menuid).getName();
            model.addAttribute("menuname", menuname);
            //根据parentId查询parentName
            String rpname = authResService.getById(authRes.getParentId()).getName();
            model.addAttribute("rpname", rpname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "res/resdetail";
    }

    //增加资源页面
    @RequestMapping(value = "/authres/resadd")
    public String resadd(Model model) throws Exception {
        //查询菜单
        List<Menu> list = menuService.findAll();
        model.addAttribute("list", list);
        return "res/resadd";
    }

    //保存资源信息
    @RequestMapping(value="/authres/ressave", method = RequestMethod.POST)
    @ResponseBody
    public String ressave(AuthRes authRes) throws Exception{
        if(authRes != null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //通过用户名获取用户Id
            int userId = authUserService.getUserIdByUserName(userDetails.getUsername());
            authRes.setCrtUserId(userId);
            Date date = new Date();
            authRes.setCrtDate(date);
            authRes.setModifyDate(null);
            authRes.setModifyUserId(null);
            authResService.save(authRes);
        }
        return "success";
    }

    //编辑资源界面
    @RequestMapping(value = "/authres/resedit")
    public String resedit(Integer resid,Model model) throws Exception {
        //查询菜单
        List<Menu> list = menuService.findAll();
        model.addAttribute("list", list);
        //根据userid查询用户信息
        authRes = authResService.getById(resid);
        model.addAttribute("ar", authRes);
        //根据parentId查询partentName
        if(authRes.getParentId() != 0 ){
            String rpname = authResService.getById(authRes.getParentId()).getName();
            model.addAttribute("rpname", rpname);
        }else{
            model.addAttribute("rpname", "自身");
        }
        //根据用户Id查询用户名
		/*if(authRes.getCrtUserId() != null){
			String rusername = authUserService.getById(authRes.getCrtUserId()).getUserName();
			model.addAttribute("rusername", rusername);
		}else{
			model.addAttribute("rusername", "default");
		}*/
        return "res/resedit";
    }

    //更新资源信息
    @RequestMapping(value="/authres/resupdate", method = RequestMethod.POST)
    @ResponseBody
    public String resupdate(AuthRes authRes) throws Exception{
        if(authRes != null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            authRes.setModifyDate(date);
            //通过用户名获取用户Id
            int userId = authUserService.getUserIdByUserName(userDetails.getUsername());
            int crtUserId = authRes.getCrtUserId();
            if(crtUserId == userId ){
                authRes.setModifyUserId(userId);
                authResService.update(authRes);
                return "success";
            }else{
                return "false";
            }
            //authResService.update(authRes);
        }
        return "success";
    }

    //批量删除资源
    @RequestMapping(value = "/authres/resdelete", method = RequestMethod.GET)
    @ResponseBody
    public String resdelete(String KeyValue) throws Exception {
        String[] items = KeyValue.split(",");
        List<String> roleList = new ArrayList<String>();
        Collections.addAll(roleList, items);
        authResIds = new ArrayList<Integer>();
        //将String List转换成Integer List
        for(String str : roleList) {
            int i = Integer.parseInt(str);
            authResIds.add(i);
        }

        //删除之前需要先检查关联性
        int count = 0;
        for(int i = 0; i < authResIds.size(); i++){
            int countref = authResRoleService.getCountByResId(authResIds.get(i));
            count += countref;
        }
        if(count > 0){
            return "fail";
        }else{
            if(authResIds != null && authResIds.size() > 0){
                authResService.batchDelete(authResIds);
            }
            return "success";
        }

    }

    //查看父类资源类型页面
    @RequestMapping(value = "/authres/resparent")
    public String resparent(Model model) {
        try {
            //查询所有的res（3类）
            List<AuthRes> list = authResService.queryAll();
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "res/resparent";
    }

    //查看资源类型页面
    @RequestMapping(value = "/authres/restype")
    public String restype(Model model) {
        try {
            //查询所有的res（3类）
            List<AuthRes> list = authResService.queryAll();
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "res/restype";
    }

}
