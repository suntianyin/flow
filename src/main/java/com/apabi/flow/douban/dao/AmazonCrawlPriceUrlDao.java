package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonCrawlPriceUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-24 15:50
 **/
@Mapper
@Repository
public interface AmazonCrawlPriceUrlDao {
    List<AmazonCrawlPriceUrl> findAll();

    List<AmazonCrawlPriceUrl> findWithoutCrawled();

    int countWithoutCrawled();

    AmazonCrawlPriceUrl findById(String url);

    int delete(String url);

    int insert(AmazonCrawlPriceUrl amazonCrawlPriceUrl);

    int update(AmazonCrawlPriceUrl amazonCrawlPriceUrl);

    int updateHasCrawled(String url);
}
