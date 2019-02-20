package com.apabi.flow.douban.service;

import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.model.DoubanMeta;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author pipi
 * @date 2018/8/8 16:23
 * @description
 */
public interface DoubanMetaService {
    DoubanMeta searchDoubanMetaByISBN(String isbn);
    List<DoubanMeta> searchDoubanMetasByISBN(String isbn13);
    List<ApabiBookMetaDataTemp> searchMetaDataTempsByISBN(String isbn13);
    // douban查询功能
    DoubanMeta searchDoubanMetaById(String doubanId);
    Page<DoubanMeta> searchDoubanMetaByPage(Map<String,String> params);
    Page<DoubanMeta> searchDoubanMetaByPageOrderByUpdateTime(Map<String,String> params);
    void addDoubanMeta(DoubanMeta doubanMeta);
    void deleteDoubanMeta(String doubanId);
    void updateDoubanMeta(DoubanMeta doubanMeta);
    List<ApabiBookMetaDataTemp> searchMetaDataTempsByISBNMultiThread(String isbn13);
    void reUpdateDoubanByCrawl();
    int countTotal();
}
