package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangCrawlPriceUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-19 17:13
 **/
@Mapper
@Repository
public interface DangdangCrawlPriceUrlDao {
    int findById(String url);
    List<DangdangCrawlPriceUrl> findAll();
    Page<DangdangCrawlPriceUrl> findWithoutCrawledByPage();
    int count();
    int countWithoutCrawled();
    void delete(String url);
    void insert(DangdangCrawlPriceUrl dangdangCrawlPriceUrl);
    void update(DangdangCrawlPriceUrl dangdangCrawlPriceUrl);
    void updateHasCrawled(String url);
}
