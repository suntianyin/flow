package com.apabi.flow.admin.service;

import java.util.List;

import com.apabi.flow.admin.model.Menu;

import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;

public interface MenuService {

	/**
	 * 入库
	 */
	int save(Menu menu);
	
	/**
	 * 更新
	 */
	int update(Menu menu) ;
	
	/**
	 * 根据ID删除
	 */
	int deleteById(Integer id);
	
	/**
	 * 批量删除
	 */
	int batchDelete(List<Integer> ids);
	
	/**
	 * 根据ID查询
	 */
	Menu getById(Integer id);
	
	/**
	 * 分页查询
	 */
	Page<Menu> findByPage(PageInfo pageInfo);
	/**
	 * 全部查询
	 */
	List<Menu> findAll();
}
