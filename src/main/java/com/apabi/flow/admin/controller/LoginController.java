package com.apabi.flow.admin.controller;

import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.Menu;
import com.apabi.flow.admin.service.AuthUserService;
import com.apabi.flow.admin.service.MenuService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthUserService authUserService;

    @RequestMapping("/login")
    public String login(){
        return "common/login";
    }
    @RequestMapping("/index")
    public String index(Model model){
        //查找所有的菜单和用户具有的模块
        List<Menu> listmenu = menuService.findAll();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AuthRes> listres = authUserService.getUserModuleResByUserName(userDetails.getUsername());
        JSONArray array = new JSONArray();
        JSONObject obj1 = new JSONObject();
        obj1.put("ModuleId", "M0");
        obj1.put("ParentId", "0");
        obj1.put("FullName", "快速开发平台");
        obj1.put("Location", "");
        array.add(obj1);
        for(Menu menu : listmenu) {
            JSONObject obj = new JSONObject();
            obj.put("ModuleId", "M"+menu.getId());
            obj.put("ParentId", "M"+menu.getParentId());
            obj.put("FullName", menu.getName());
            obj.put("Location", "");
            array.add(obj);
        }
        for(AuthRes authRes : listres){
            JSONObject obj = new JSONObject();
            obj.put("ModuleId", "R"+authRes.getId());
            obj.put("ParentId", "M"+authRes.getMenuId());
            obj.put("FullName", authRes.getName());
            obj.put("Location", authRes.getUrl());
            array.add(obj);
        }
        model.addAttribute("data", array.toString());
        String username = userDetails.getUsername();
        model.addAttribute("username", username);
        return "common/index";
    }
    @RequestMapping("/home")
    public String home(){
        return "common/home";
    }

    //系统退出
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public String logout(HttpServletRequest request){
        //使当前用户的session失效
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDetails.getUsername();
        request.getSession().invalidate();
        return "logout";
    }

}
