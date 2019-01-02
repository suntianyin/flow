package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonItemUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-24 16:33
 **/
@Mapper
@Repository
public interface AmazonItemUrlDao {
    Page<AmazonItemUrl> findWithoutCrawledByPage();

    AmazonItemUrl findById(String url);

    int count();

    int countWithoutCrawled();

    int delete(String url);

    int insert(AmazonItemUrl amazonItemUrl);

    int update(AmazonItemUrl amazonItemUrl);

    int updateHasCrawled(String url);
}
