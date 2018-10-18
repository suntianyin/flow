package com.apabi.flow.processing.dao;

import com.apabi.flow.processing.model.OutUnit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OutUnitMapper {
    int deleteByPrimaryKey(String unitId);

    int insert(OutUnit record);

    int insertSelective(OutUnit record);

    OutUnit selectByPrimaryKey(String unitId);

    int updateByPrimaryKeySelective(OutUnit record);

    int updateByPrimaryKey(OutUnit record);

    List<OutUnit> selectAll();
}