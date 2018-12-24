package com.apabi.flow.xinhuashudaun.dao;

import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-18 17:33
 **/
@Mapper
@Repository
public interface XinhuashudianItemUrlDao {
    int delete(String url);

    int insert(XinhuashudianItemUrl record);

    XinhuashudianItemUrl findById(String url);

    Page<XinhuashudianItemUrl> findWithoutCrawled();

    int update(XinhuashudianItemUrl record);

    int count();

    int countWithoutCrawled();

    void updateHasCrawled(String url);
}
