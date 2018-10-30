package com.apabi.flow.cleanData.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by pipi on 2018/10/30.
 */
@Mapper
@Repository
public interface NlcCrawlIsbnDao {
    void delete(String isbn);
    void insert(String isbn);
    int count();
}
