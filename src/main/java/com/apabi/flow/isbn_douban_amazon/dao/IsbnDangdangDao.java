package com.apabi.flow.isbn_douban_amazon.dao;

import com.apabi.flow.isbn_douban_amazon.model.IsbnDangdang;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2019-1-18 10:43
 **/
@Mapper
@Repository
public interface IsbnDangdangDao {
    int insert(IsbnDangdang isbnDangdang);

    int delete(String isbn);

    int update(IsbnDangdang isbnDangdang);

    int count();

    int countShouldCrawledInDouban();

    int countShouldCrawledInAmazon();

    int countShouldCrawledInNlc();

    IsbnDangdang findById(String isbn);

    Page<IsbnDangdang> findByPage();

    Page<IsbnDangdang> findByPageShouldCrawledInDouban();

    Page<IsbnDangdang> findByPageShouldCrawledInAmazon();

    Page<IsbnDangdang> findByPageShouldCrawledInNlc();

    void updateAmazonHasCrawled(String isbn);

    void updateDoubanHasCrawled(String isbn);

    void updateNlcHasCrawled(String isbn);

}
