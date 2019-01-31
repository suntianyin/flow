package com.apabi.flow.log.dao;

import com.apabi.flow.log.model.OperateLog;

public interface OperateLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(OperateLog record);

    int insertSelective(OperateLog record);

    OperateLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OperateLog record);

    int updateByPrimaryKeyWithBLOBs(OperateLog record);

    int updateByPrimaryKey(OperateLog record);
}