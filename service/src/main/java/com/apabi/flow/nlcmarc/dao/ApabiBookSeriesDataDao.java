package com.apabi.flow.nlcmarc.dao;

import com.apabi.flow.nlcmarc.model.ApabiBookSeriesData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by pipi on 2018/10/10.
 */
@Mapper
@Repository
public interface ApabiBookSeriesDataDao {
    ApabiBookSeriesData findById(String id);
    void delete(String id);
    void insert(ApabiBookSeriesData apabiBookSeriesData);
    void update(ApabiBookSeriesData apabiBookSeriesData);
}
