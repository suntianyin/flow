package com.apabi.shuyuan.book.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author guanpp
 * @date 2018/11/5 15:25
 * @description
 */
@Repository
@Mapper
public interface CmfBookMetaDao {

    //获取最大drid
    Integer getMaxDrid();

    //通过文件名获取id
    String getMetaIdByFileName(String fileName);
}
