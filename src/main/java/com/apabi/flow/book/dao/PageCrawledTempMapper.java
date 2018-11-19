package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.PageCrawledTemp;
import com.github.pagehelper.Page;

import java.util.List;

public interface PageCrawledTempMapper {
    int insert(PageCrawledTemp record);

    int insertSelective(PageCrawledTemp record);

    int deleteByPrimaryKey(String id);

    int deleteByIdAndPage(PageCrawledTemp record);

    List<PageCrawledTemp> findAll();

    Page<PageCrawledTemp> pageAll();

    void deleteAll();
}