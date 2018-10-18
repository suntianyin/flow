package com.apabi.flow.admin.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.apabi.flow.admin.model.Menu;
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


@Controller("menu")
@RequestMapping(value="/admin/auth")
public class MenuController {
	
	private Logger log = LoggerFactory.getLogger(MenuController.class);
	public static final String COUNT_QUERY_STATEMENT = "count";
	
	@Autowired
	private MenuService menuService;
	@Autowired
	private AuthResService authResService;
	@Autowired
	private AuthUserService authUserService;
	
	private Menu menu;
	private List<Integer> menuIds;

	private Page page;
	
	//菜单展示页面
	@RequestMapping(value = "/authmenu/menu")
	public String menu(Model model) {
		return "menu/menu";
	}

	@RequestMapping(value = "/authmenu/menudata")
	@ResponseBody
	public PageInfo user(PageInfo pageInfo, String keywords) {
		try {
			long start = System.currentTimeMillis();
			pageInfo.getFilters().put("keywords", keywords);
			Page<Menu> menu = menuService.findByPage(pageInfo);
			pageInfo = new PageInfo<>(menu);
			long end = System.currentTimeMillis();
			log.info("用户信息列表查询耗时：" + (end - start) + "毫秒");
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
		return pageInfo;
	}

	//增加菜单页面
	@RequestMapping(value = "/authmenu/menuadd")	
	public String menuadd(Model model) throws Exception {		
		return "menu/menuadd";
	}
	
	//保存菜单信息
	@RequestMapping(value="/authmenu/menusave", method = RequestMethod.POST)
	@ResponseBody
	public String menusave(Menu menu) throws Exception{
		if(menu != null) {	
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			//通过用户名获取用户Id
			int userId = authUserService.getUserIdByUserName(userDetails.getUsername());
			menu.setCrtUserId(userId);
			Date date = new Date();				
			menu.setCrtDate(date);			
			menu.setModifyDate(null);			
			menu.setModifyUserId(null);			
			menuService.save(menu);
		}
		return "success";
	}
	
	//编辑菜单界面
	@RequestMapping(value = "/authmenu/menuedit")	
	public String menuedit(Integer menuid,Model model) throws Exception {						
		//根据menuid查询菜单信息
		menu = menuService.getById(menuid);
		model.addAttribute("mn", menu);
		//根据parentId查询partentName
		if(menu.getParentId() != 0 ){
			String mpname = menuService.getById(menu.getParentId()).getName();
			model.addAttribute("mpname", mpname);
		}else{
			model.addAttribute("mpname", "自身");
		}		
		return "menu/menuedit";
	}
	
	//更新菜单信息
	@RequestMapping(value="/authmenu/menuupdate", method = RequestMethod.POST)
	@ResponseBody
	public String menuupdate(Menu menu) throws Exception{
		if(menu != null) {	
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			//通过用户名获取用户Id
			int userId = authUserService.getUserIdByUserName(userDetails.getUsername());			
			int crtUserId = menu.getCrtUserId();
			Date date = new Date();	
			if(crtUserId == userId ){
				menu.setModifyDate(date);
				menu.setModifyUserId(userId);
				menuService.update(menu);
				return "success";
			}else{					
				return "false";
			}
			//menu.setModifyDate(date);						
			//menuService.update(menu);
		}
		return "success";
	}
	
	//批量删除菜单
	@RequestMapping(value = "/authmenu/menudelete", method = RequestMethod.GET)
	@ResponseBody
	public String menudelete(String KeyValue) throws Exception {		
		String[] items = KeyValue.split(",");
		List<String> menuList = new ArrayList<String>();		
		Collections.addAll(menuList, items);
		menuIds = new ArrayList<Integer>();
		//将String List转换成Integer List
		for(String str : menuList) {
			  int i = Integer.parseInt(str);
			  menuIds.add(i);
		}
		//删除之前需要先检查关联性
		int count = 0;
		for(int i = 0; i < menuIds.size(); i++){
			int countref = authResService.getCountByMenuId(menuIds.get(i));			
			count += countref;
		}
		if(count > 0){
			return "fail";
		}else{
			if(menuIds != null && menuIds.size() > 0){			
				menuService.batchDelete(menuIds);
			}
			return "success";
		}
	}
	
	//菜单详情页面
	@RequestMapping(value = "/authmenu/menudetail")	
	public String menudetail(Integer menuid,Model model) {		
		try {
			//根据menuid查询菜单信息
			menu = menuService.getById(menuid);
			model.addAttribute("mn", menu);			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return "menu/menudetail";
	}
	
	//查看父类资源类型页面
	@RequestMapping(value = "/authmenu/menuparent")	
	public String menuparent(Model model) {		
		try {			
			List<Menu> list = menuService.findAll();
			model.addAttribute("list", list);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return "menu/menuparent";
	}

}
