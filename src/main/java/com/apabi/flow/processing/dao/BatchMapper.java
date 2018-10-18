package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.Batch;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BatchMapper {
    int deleteByPrimaryKey(String id);

    int insert(Batch record) throws DataAccessException;

    int insertSelective(Batch record) throws DataAccessException;

    Batch selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Batch record) throws DataAccessException;

    int updateByPrimaryKey(Batch record) throws DataAccessException;

    List<Batch> listBatchSelective(Map map);

    Page<Batch> listBatchSelectiveByPage(Map map);
}