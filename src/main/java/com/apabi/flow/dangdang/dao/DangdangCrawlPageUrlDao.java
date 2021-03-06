package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangCrawlPageUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-7 14:25
 **/
@Mapper
@Repository
public interface DangdangCrawlPageUrlDao {

    int countWithoutCrawled();

    Page<DangdangCrawlPageUrl> findWithoutCrawledByPage();

    void updateHasCrawled(String url);

    List<DangdangCrawlPageUrl> findAll();

    int delete(String url);

    int insert(DangdangCrawlPageUrl dangdangCrawlPageUrl);

    DangdangCrawlPageUrl findById(String url);

    int update(DangdangCrawlPageUrl dangdangCrawlPageUrl);
}
