package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangCrawlPricePageUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-21 11:07
 **/
@Mapper
@Repository
public interface DangdangCrawlPricePageUrlDao {
    DangdangCrawlPricePageUrl findById(String url);
    int delete(String url);
    int insert(DangdangCrawlPricePageUrl record);
    int count();
    int update(DangdangCrawlPricePageUrl record);
    void updateHasCrawled(String url);
    int countWithoutCrawled();
    List<DangdangCrawlPricePageUrl> findAll();
    Page<DangdangCrawlPricePageUrl> findWithoutCrawled();
}
