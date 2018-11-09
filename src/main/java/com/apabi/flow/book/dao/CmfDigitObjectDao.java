package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.CmfDigitObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author guanpp
 * @date 2018/11/7 9:55
 * @description
 */
@Repository
@Mapper
public interface CmfDigitObjectDao {

    //新增数据
    int insertCmfDigitObject(CmfDigitObject cmfDigitObject) throws Exception;

    //获取数据
    CmfDigitObject findCmfDigitObjectByFileId(Integer fileId) throws Exception;
}
