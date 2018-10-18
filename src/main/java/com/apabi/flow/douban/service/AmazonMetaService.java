package com.apabi.flow.douban.service;

import com.apabi.flow.douban.model.AmazonMeta;

/**
 * Created by pipi on 2018/9/4.
 */
public interface AmazonMetaService {
    AmazonMeta findOrCrawlAmazonMetaByIsbn(String isbn) throws Exception;
    void updateAmazon(AmazonMeta amazonMeta);
}
