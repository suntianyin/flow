package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.Bibliotheca;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BibliothecaMapper {
    int deleteByPrimaryKey(String id);

//    @Deprecated
//    int insert(Bibliotheca record) throws DataAccessException;

    int insertSelective(Bibliotheca record) throws DataAccessException;

    Bibliotheca selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Bibliotheca record) throws DataAccessException;

//    int updateByPrimaryKey(Bibliotheca record);

    int updateByBatchIdAndState(Map<String, Object> map) throws DataAccessException;

    List<Bibliotheca> listBibliothecaSelective(Map map);

    Page<Bibliotheca> listBibliothecaSelectiveByPage(Map map);
}