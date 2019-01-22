package com.apabi.flow.auth.dao;

import com.apabi.flow.auth.model.Resource;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ResourceMapper {
    int deleteByPrimaryKey(Integer resrId);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Integer resrId);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);

    Page<Resource> listResource(Map<String, Object> paramsMap);

    List<Resource> listResource1(Map<String, Object> paramsMap);

    int updateByBooklistNum(@Param("booklistNum") String booklistNum,@Param("batchNum") String batchNum);

    int updateByBatchNumAndMetaId(Resource record);
}