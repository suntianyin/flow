package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonMeta;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author pipi
 * @date 2018/9/4 13:52
 * @description
 */
@Mapper
@Repository
public interface AmazonMetaDao {
    // 根据amazonId查找AmazonMeta
    AmazonMeta getAmazonMetaByAmazonId(String amazonId);
    // 根据isbn10查找AmazonMeta
    AmazonMeta getAmazonMetaByIsbn10(String isbn10);
    // 根据isbn13查找AmazonMeta
    AmazonMeta getAmazonMetaByIsbn13(String isbn13);
    // 添加AmazonMeta
    void addAmazonMeta(AmazonMeta amazonMeta);
    // 更新AmazonMeta
    void updateAmazonMeta(AmazonMeta amazonMeta);
    // 分页查询AmazonMeta
    Page<AmazonMeta> findAmazonMetaByPage(Map<String,String> params);
}