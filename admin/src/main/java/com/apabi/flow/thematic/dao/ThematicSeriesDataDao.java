package com.apabi.flow.thematic.dao;

import com.apabi.flow.thematic.model.ThematicSeriesData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/8/22.
 */
@Repository
@Mapper
public interface ThematicSeriesDataDao {
    @Select("SELECT * FROM APABI_THEMATIC_SERIES_DATA WHERE ID = #{id}")
    ThematicSeriesData findThematicSeriesDataById(String id);

    @Select("SELECT * FROM APABI_THEMATIC_SERIES_DATA WHERE THEMATICID = #{thematicId}")
    List<ThematicSeriesData> findAllThematicSeriesDataByThematicSeriesId(String thematicId);

    @Select("SELECT * FROM APABI_THEMATIC_SERIES_DATA")
    List<ThematicSeriesData> findAllThematicSeriesData();

    @Insert("INSERT INTO APABI_THEMATIC_SERIES_DATA(ID,THEMATICID,METAID,TITLE,AUTHOR,ISBN,ISBN13,OPERATOR,CREATETIME,UPDATETIME,PUBLISHER) VALUES(#{id},#{thematicId},#{metaId},#{title},#{author},#{isbn},#{isbn13},#{operator},#{createTime},#{updateTime},#{publisher})")
    void addThematicSeriesData(ThematicSeriesData thematicSeriesData);

    @Select("SELECT * FROM APABI_THEMATIC_SERIES_DATA WHERE THEMATICID = #{thematicId}")
    List<ThematicSeriesData> findThematicSeriesDataByIdPage(String thematicId);
}
