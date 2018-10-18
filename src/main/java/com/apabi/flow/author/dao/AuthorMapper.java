package com.apabi.flow.author.dao;

import com.apabi.flow.author.model.Author;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author supeng
 */
public interface AuthorMapper {
    int deleteByPrimaryKey(String id);

//    int insert(Author record);

    int insertSelective(Author record);

    Author selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Author record);

//    int updateByPrimaryKey(Author record);

    List<Author> findAuthorsByIdAndTitle(@Param("id") String id,@Param("title") String title);

    Page<Author> findAuthorByPage(@Param("id") String id,@Param("title") String title);
}