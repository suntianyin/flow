package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.AssemblyResult;

public interface AssemblyResultMapper {
    int deleteByPrimaryKey(String id);

    int insert(AssemblyResult record);

    int insertSelective(AssemblyResult record);

    AssemblyResult selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AssemblyResult record);

    int updateByPrimaryKey(AssemblyResult record);
}