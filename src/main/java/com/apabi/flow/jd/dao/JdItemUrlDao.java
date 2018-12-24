package com.apabi.flow.jd.dao;

import com.apabi.flow.jd.model.JdItemUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-04 11:47
 **/
@Mapper
@Repository
public interface JdItemUrlDao {
    int delete(String url);

    int insert(JdItemUrl jdItemUrl);

    JdItemUrl findById(String url);

    int update(JdItemUrl jdItemUrl);

    Page<JdItemUrl> findByPage();

    Page<JdItemUrl> findByPageWithoutCrawled();

    int count();

    int countWithoutCrawled();

    void updateHasCrawled(String url);
}