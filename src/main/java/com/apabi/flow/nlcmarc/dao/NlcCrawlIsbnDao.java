package com.apabi.flow.nlcmarc.dao;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by pipi on 2018/10/30.
 */
@Mapper
@Repository
public interface NlcCrawlIsbnDao {
    Page<String> getIsbnList();
    Page<String> getSuspectIsbnList();
    void delete(String isbn);
    void insert(String isbn);
    int count();
    int countSuspect();
    void updateHasCrawled(String isbn);
    void markIsbnSuspect(String isbn);
}
