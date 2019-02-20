package com.apabi.flow.douban.service;

import com.apabi.flow.douban.model.AmazonMeta;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 * Created by pipi on 2018/9/4.
 */
public interface AmazonMetaService {
    AmazonMeta findOrCrawlAmazonMetaByIsbn(String isbn) throws Exception;
    void updateAmazon(AmazonMeta amazonMeta);
    Page<AmazonMeta> findAmazonMetaByPage(Map<String,String> params);
    Page<AmazonMeta> findAmazonMetaByPageOrderByUpdateTime(Map<String,String> params);
    AmazonMeta findById(String amazonId);
    int countTotal();
}
