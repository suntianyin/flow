package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.DoubanMeta;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author pipi
 * @date 2018/9/29 18:12
 * @description
 */
@Repository
@Mapper
public interface DoubanMetaDao {
    int countShouldUpdate();
    int count();
    DoubanMeta findById(String doubanId);
    void update(DoubanMeta doubanMeta);
    void insert(DoubanMeta doubanMeta);
    void deleteById(String doubanId);
    Page<DoubanMeta> findByPage(Map<String,String> params);
    Page<DoubanMeta> findByPageOrderByUpdateTime(Map<String,String> params);
    Page<DoubanMeta> findByPageOrderByDoubanId();
    List<DoubanMeta> findByIsbn13(String isbn13);
    List<DoubanMeta> findByIsbn10(String isbn10);
    Page<DoubanMeta> findShouldUpdate();
}
