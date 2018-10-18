package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonCrawlUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/17 10:27
 **/
@Repository
@Mapper
public interface AmazonCrawlUrlDao {
    List<String> findAllUrl();
    void insert(AmazonCrawlUrl amazonCrawlUrl);
}
