package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.PageAssemblyQueue;

import java.util.List;

public interface PageAssemblyQueueMapper {
    int deleteByPrimaryKey(String id);

    int insert(PageAssemblyQueue record);

    int insertSelective(PageAssemblyQueue record);

    List<PageAssemblyQueue> findAll();
}