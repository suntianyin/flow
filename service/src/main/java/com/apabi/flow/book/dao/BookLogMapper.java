package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookLog;

/**
 * 暂时记录 拉取分页信息和分页信息拼装章节的日志
 * @author supeng
 */
public interface BookLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(BookLog record);

    int insertSelective(BookLog record);

    BookLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BookLog record);

    int updateByPrimaryKey(BookLog record);
}