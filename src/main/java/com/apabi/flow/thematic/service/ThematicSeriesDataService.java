package com.apabi.flow.thematic.service;

import com.apabi.flow.thematic.model.ThematicSeriesData;

import java.util.List;

/**
 * Created by pipi on 2018/8/21.
 */
public interface ThematicSeriesDataService {
    ThematicSeriesData findThematicSeriesDataById(String id);
    List<ThematicSeriesData> findAllThematicSeriesDataByThematicSeriesId(String thematicId);
    List<ThematicSeriesData> findAllThematicSeriesData();
//    PageInfo<ThematicSeriesData> findThematicSeriesDataByIdPage(String thematicId);
}
