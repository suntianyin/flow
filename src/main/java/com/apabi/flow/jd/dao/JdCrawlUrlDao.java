package com.apabi.flow.jd.dao;

import com.apabi.flow.jd.model.JdCrawlUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-04 11:20
 **/
@Mapper
@Repository
public interface JdCrawlUrlDao {

    List<JdCrawlUrl> findAllWithNoPage();

    List<JdCrawlUrl> findAll();

    int delete(String url);

    int insert(JdCrawlUrl jdCrawlUrl);

    JdCrawlUrl findById(String url);

    int update(JdCrawlUrl jdCrawlUrl);
}