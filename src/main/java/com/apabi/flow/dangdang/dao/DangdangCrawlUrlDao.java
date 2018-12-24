package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangCrawlUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-7 14:04
 */
@Mapper
@Repository
public interface DangdangCrawlUrlDao {

    List<DangdangCrawlUrl> findAll();

    int delete(String url);

    int insert(DangdangCrawlUrl dangdangCrawlUrl);

    DangdangCrawlUrl findById(String url);

    int update(DangdangCrawlUrl dangdangCrawlUrl);
}
