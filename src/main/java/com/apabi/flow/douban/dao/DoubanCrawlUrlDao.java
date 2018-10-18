package com.apabi.flow.douban.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/15 17:23
 **/
@Repository
@Mapper
public interface DoubanCrawlUrlDao {
    List<String> findAllUrl();
}
