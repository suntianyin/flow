package com.apabi.flow.admin.service.impl;

import java.util.List;

import com.apabi.flow.admin.dao.MenuDao;
import com.apabi.flow.admin.model.Menu;
import com.apabi.flow.admin.service.MenuService;
import com.apabi.flow.common.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuServiceImpl implements MenuService {

	private Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
	
	@Autowired
	private MenuDao menuDao;

	//入库
	@Transactional(propagation = Propagation.REQUIRED)
	public int save(Menu menu) {
		return menuDao.save(menu);
	}
	
	//更新
	@Transactional(propagation = Propagation.REQUIRED)
	public int update(Menu menu){
		return menuDao.update(menu);
	}
	
	//根据ID删除
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteById(Integer id) {
		return menuDao.deleteById(id);
	}
	
	//批量删除
	@Transactional(propagation = Propagation.REQUIRED)
	public int batchDelete(List<Integer> ids) {
		return menuDao.batchDelete(ids);
	}
	
	//根据ID查询
	public Menu getById(Integer id) {
		return menuDao.getById(id);
	}

	//分页查询
	@Override
	public Page<Menu> findByPage(PageInfo pageInfo) {
		PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
		return menuDao.findByPage(pageInfo.getFilters());
	}

	public List<Menu> findAll() {
		return menuDao.queryAll();
	}
}
