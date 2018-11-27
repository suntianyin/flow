package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.PageAssemblyQueue;
import com.github.pagehelper.Page;

import java.util.List;

public interface PageAssemblyQueueMapper {
    int deleteByPrimaryKey(String id);

    int insert(PageAssemblyQueue record);

    int insertSelective(PageAssemblyQueue record);

    List<PageAssemblyQueue> findAll();

    Page<PageAssemblyQueue> pageAll();

    int deleteAll();
}