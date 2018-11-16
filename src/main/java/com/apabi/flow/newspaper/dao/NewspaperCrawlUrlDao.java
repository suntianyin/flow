package com.apabi.flow.newspaper.dao;

import com.apabi.flow.newspaper.crawl_url.model.NewspaperCrawlUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/11/7.
 */
@Mapper
@Repository
public interface NewspaperCrawlUrlDao {
    List<NewspaperCrawlUrl> findAllBySource(String source);
    NewspaperCrawlUrl findByIndexPage(String indexPage);
    void delete(String indexPage);
    void insert(NewspaperCrawlUrl newspaperCrawlUrl);
    void update(NewspaperCrawlUrl newspaperCrawlUrl);
}

