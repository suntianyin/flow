package com.apabi.shuyuan.book.dao;

import com.apabi.shuyuan.book.model.SCmfDigitObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/11/7 13:21
 * @description
 */
@Repository
@Mapper
public interface SCmfDigitObjectDao {

    //获取数据
    SCmfDigitObject findSCmfDigitObjectByFileId(Integer fileId) throws Exception;

    //获取数据
    List<SCmfDigitObject> findSCmfDigitObjectByDrid(Integer drid)throws Exception;
}
