package com.apabi.flow.admin.dao;

import java.util.List;
import java.util.Map;

import com.apabi.flow.admin.model.Menu;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

public interface MenuDao {
	
	/**
	 * 入库
	 */
	int save(Menu menu) ;
	
	/**
	 * 更新
	 */
	int update(Menu menu) ;
	
	/**
	 * 根据ID删除
	 */
	int deleteById(Integer id) ;
	
	/**
	 * 根据ID批量删除
	 */
	int batchDelete(@Param("ids") List<Integer> ids) ;
	
	/**
	 * 根据ID查询
	 */
	Menu getById(Integer id);
	
	/**
	 * 分页查询
	 */
	Page<Menu> findByPage(Map<String,Object> filters);
	/**
	 * 全部查询
	 */
	List<Menu> queryAll();
}
