package com.apabi.flow.thematic.dao;

import com.apabi.flow.thematic.model.ThematicSeries;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/8/21.
 */
@Repository
@Mapper
public interface ThematicSeriesDao {
    @Select("SELECT * FROM APABI_THEMATIC_SERIES WHERE ID = #{id}")
    ThematicSeries findThematicSeriesById(String id);

    @Insert("INSERT INTO APABI_THEMATIC_SERIES(id,title,dataSource,collator,operator,createTime,updateTime) VALUES(#{id},#{title},#{dataSource},#{collator},#{operator},#{createTime},#{updateTime})")
    int addThematicSeries(ThematicSeries thematicSeries);

    @Select("SELECT * FROM APABI_THEMATIC_SERIES")
    List<ThematicSeries> findAllThematicSeries();

    @Update("UPDATE APABI_THEMATIC_SERIES SET TITLE=#{title},DATASOURCE=#{dataSource},COLLATOR=#{collator},OPERATOR=#{operator},UPDATETIME=#{updateTime} WHERE ID=#{id}")
    void updateThematicSeries(ThematicSeries thematicSeries);
}
