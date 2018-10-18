package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.UserUnit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author supeng
 */
@Repository
@Mapper
public interface UserUnitMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserUnit record);

    int insertSelective(UserUnit record);

    UserUnit selectByPrimaryKey(String id);

    UserUnit selectByUserId(Integer id);

    int updateByPrimaryKeySelective(UserUnit record);

    int updateByPrimaryKey(UserUnit record);
}