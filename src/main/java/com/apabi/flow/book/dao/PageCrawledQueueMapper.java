package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.PageCrawledQueue;
import com.github.pagehelper.Page;

import java.util.List;

public interface PageCrawledQueueMapper {
    int insert(PageCrawledQueue record);

    int insertSelective(PageCrawledQueue record);

    int deleteByPrimaryKey(String id);

    List<PageCrawledQueue> findAll();

    Page<PageCrawledQueue> pageAll();

    void deleteAll();
}