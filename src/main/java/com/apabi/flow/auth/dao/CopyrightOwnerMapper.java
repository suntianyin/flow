package com.apabi.flow.auth.dao;

import com.apabi.flow.auth.model.CopyrightOwner;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface CopyrightOwnerMapper {
    int deleteByPrimaryKey(String id);

    int insert(CopyrightOwner record);

    int insertSelective(CopyrightOwner record);

    CopyrightOwner selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CopyrightOwner record);

    int updateByPrimaryKey(CopyrightOwner record);

    Page<CopyrightOwner> listCopyrightOwner(Map<String, Object> paramsMap);

    List<CopyrightOwner> findAll();
}