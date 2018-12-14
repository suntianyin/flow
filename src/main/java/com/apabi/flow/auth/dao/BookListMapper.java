package com.apabi.flow.auth.dao;

import com.apabi.flow.auth.model.BookList;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BookListMapper {
    int deleteByPrimaryKey(String id);

    int insert(BookList record);

    int insertSelective(BookList record);

    BookList selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BookList record);

    int updateByPrimaryKey(BookList record);

    List<BookList> findAll();

    Page<BookList> listBookList(Map<String, Object> paramsMap);

    int updateFileNameAndFilePathById(@Param("id")String id, @Param("fileName")String fileName, @Param("filePath")String filePath);
}