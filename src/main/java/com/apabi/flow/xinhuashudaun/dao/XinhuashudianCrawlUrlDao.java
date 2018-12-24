package com.apabi.flow.xinhuashudaun.dao;

import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-14 14:56
 **/
@Mapper
@Repository
public interface XinhuashudianCrawlUrlDao {
    int delete(String url);

    int insert(XinhuashudianCrawlUrl record);

    XinhuashudianCrawlUrl findById(String url);

    List<XinhuashudianCrawlUrl> findAll();

    int update(XinhuashudianCrawlUrl record);

    int count();

    int countWithoutCrawled();
}
