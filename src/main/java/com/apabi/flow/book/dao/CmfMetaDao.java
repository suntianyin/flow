package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.CmfMeta;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author guanpp
 * @date 2018/11/6 17:18
 * @description
 */
@Repository
@Mapper
public interface CmfMetaDao {

    //新增数据
    int insertCmfMeta(CmfMeta cmfMeta) throws Exception;

    //获取数据
    CmfMeta findCmfMetaByDrid(Integer drId) throws Exception;
}
