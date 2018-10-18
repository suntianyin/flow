package com.apabi.flow.thematic.service.impl;

import com.apabi.flow.thematic.dao.ThematicSeriesDataDao;
import com.apabi.flow.thematic.model.ThematicSeriesData;
import com.apabi.flow.thematic.service.ThematicSeriesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/21 17:56
 **/
@Service
public class ThematicSeriesDataServiceImpl implements ThematicSeriesDataService {

    @Autowired
    ThematicSeriesDataDao thematicDataMapper;

    @Override
    public ThematicSeriesData findThematicSeriesDataById(String id) {
        ThematicSeriesData thematicSeriesData = thematicDataMapper.findThematicSeriesDataById(id);
        return thematicSeriesData;
    }

    @Override
    public List<ThematicSeriesData> findAllThematicSeriesDataByThematicSeriesId(String thematicId) {
//        Page<Object> page = PageHelper.startPage(1, 10);
        List<ThematicSeriesData> thematicSeriesDataList = thematicDataMapper.findAllThematicSeriesDataByThematicSeriesId(thematicId);
        return thematicSeriesDataList;
    }

    @Override
    public List<ThematicSeriesData> findAllThematicSeriesData() {
        List<ThematicSeriesData> allThematicSeriesData = thematicDataMapper.findAllThematicSeriesData();
        return allThematicSeriesData;
    }

   /* @Override
    public PageInfo<ThematicSeriesData> findThematicSeriesDataByIdPage(String thematicId){
        PageHelper.startPage(1, 10);
        List<ThematicSeriesData> thematicSeriesDataByIdPage = thematicDataMapper.findThematicSeriesDataByIdPage(thematicId);
        PageInfo pageInfo = new PageInfo(thematicSeriesDataByIdPage);
        return pageInfo;
    }*/
}
