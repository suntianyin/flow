package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author guanpp
 * @date 2018/8/27 10:43
 * @description
 */
@Repository
@Mapper
public interface BookFileDao {

    //保存图书和上传文件相关信息
    int insertBookFile(BookFile bookFile);
}
