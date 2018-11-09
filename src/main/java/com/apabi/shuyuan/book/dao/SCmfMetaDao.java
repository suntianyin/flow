package com.apabi.shuyuan.book.dao;

import com.apabi.shuyuan.book.model.SCmfMeta;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author guanpp
 * @date 2018/11/5 15:25
 * @description
 */
@Repository
@Mapper
public interface SCmfMetaDao {

    //获取最大drid
    Integer getMaxDrid()throws Exception;

    //通过文件名获取id
    String getMetaIdByFileName(String fileName);

    //获取图书元数据
    SCmfMeta findSCmfBookMetaByDrid(Integer drid)throws Exception;
}
