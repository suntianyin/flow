package com.apabi.flow.jd.dao;

import com.apabi.flow.jd.model.JdCrawlPageUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-04 17:20
 **/
@Mapper
@Repository
public interface JdCrawlPageUrlDao {

    List<JdCrawlPageUrl> findAllWithoutCrawled();

    List<JdCrawlPageUrl> findAll();

    int delete(String url);

    int insert(JdCrawlPageUrl jdCrawlPageUrl);

    JdCrawlPageUrl findById(String url);

    int update(JdCrawlPageUrl record);

    void updateHasCrawled(String url);
}
