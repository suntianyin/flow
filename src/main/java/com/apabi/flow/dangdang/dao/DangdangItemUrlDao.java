package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangItemUrl;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018-12-10.
 */
@Mapper
@Repository
public interface DangdangItemUrlDao {

    int count();

    int countWithoutCrawled();

    int delete(String url);

    int insert(DangdangItemUrl dangdangItemUrl);

    DangdangItemUrl findById(String url);

    Page<DangdangItemUrl> findByPageWithoutCrawled();

    List<DangdangItemUrl> findAll();

    int update(DangdangItemUrl dangdangItemUrl);

    void updateHasCrawled(String url);
}
