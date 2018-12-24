package com.apabi.flow.xinhuashudaun.dao;

import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlPageUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-17 17:01
 **/
@Mapper
@Repository
public interface XinhuashudianCrawlPageUrlDao {
    int insert(XinhuashudianCrawlPageUrl xinhuashudianCrawlPageUrl);

    int delete(String url);

    int update(XinhuashudianCrawlPageUrl xinhuashudianCrawlPageUrl);

    void updateHasCrawled(String url);

    XinhuashudianCrawlPageUrl findById(String url);

    Page<XinhuashudianCrawlPageUrl> findByPageWithoutCrawled();

    List<XinhuashudianCrawlPageUrl> findAll();

    int count();

    int countWithoutCrawled();
}
