package com.apabi.flow.isbn_douban_amazon.dao;

import com.apabi.flow.isbn_douban_amazon.model.IsbnDoubanAmazon;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-12 10:51
 **/
@Mapper
@Repository
public interface IsbnDoubanAmazonDao {
    int count();

    int countWithoutCrawled();

    int delete(String isbn);

    int insert(IsbnDoubanAmazon isbnDoubanAmazon);

    IsbnDoubanAmazon findById(String isbn);

    int update(IsbnDoubanAmazon isbnDoubanAmazon);

    List<IsbnDoubanAmazon> findAll();

    Page<IsbnDoubanAmazon> findAllWithoutCrawledByPage();

    void updateDoubanCrawled(String isbn);

    void updateAmazonCrawled(String isbn);

    void updateStatusHasCrawled(String isbn);
}
