package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonCrawlPricePageUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-24 16:05
 **/
@Mapper
@Repository
public interface AmazonCrawlPricePageUrlDao {
    AmazonCrawlPricePageUrl findById(String url);

    Page<AmazonCrawlPricePageUrl> findByPageWithoutCrawled();

    int count();

    int countWithoutCrawled();

    int delete(String url);

    int insert(AmazonCrawlPricePageUrl amazonCrawlPricePageUrl);

    int update(AmazonCrawlPricePageUrl amazonCrawlPricePageUrl);

    int updateHasCrawled(String url);
}
