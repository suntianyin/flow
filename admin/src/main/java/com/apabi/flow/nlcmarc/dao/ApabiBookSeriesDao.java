package com.apabi.flow.nlcmarc.dao;

import com.apabi.flow.nlcmarc.model.ApabiBookSeries;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/10/11.
 */
@Mapper
@Repository
public interface ApabiBookSeriesDao {
    ApabiBookSeries findById(String id);
    List<String> findAllTitles();
    void delete();
    void insert(ApabiBookSeries apabiBookSeries);
    void update(ApabiBookSeries apabiBookSeries);
}
