package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookTask;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface BookTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(BookTask record);

    int insertSelective(BookTask record);

    BookTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BookTask record);

    int updateByPrimaryKey(BookTask record);

    //查看任务列表
    Page<BookTask> showTaskList(Map<String, Object> queryMap);
}