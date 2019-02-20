package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.AmazonMeta;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author pipi
 * @date 2018/9/4 13:52
 * @description
 */
@Mapper
@Repository
public interface AmazonMetaDao {
    /**
     * 根据amazonId查找AmazonMeta
     *
     * @param amazonId
     * @return
     */
    AmazonMeta findById(String amazonId);

    /**
     * 根据isbn10查找AmazonMeta
     *
     * @param isbn10
     * @return
     */
    List<AmazonMeta> findByIsbn10(String isbn10);

    /**
     * 根据isbn13查找AmazonMeta
     *
     * @param isbn13
     * @return
     */
    List<AmazonMeta> findByIsbn13(String isbn13);

    /**
     * 添加AmazonMeta
     *
     * @param amazonMeta
     */
    void insert(AmazonMeta amazonMeta);

    /**
     * 更新AmazonMeta
     *
     * @param amazonMeta
     */
    void update(AmazonMeta amazonMeta);

    /**
     * 分页查询AmazonMeta
     *
     * @param params
     * @return
     */
    Page<AmazonMeta> findByPage(Map<String, String> params);

    /**
     *
     * @param params
     * @return
     */
    Page<AmazonMeta> findByPageOrderByUpdateTime(Map<String, String> params);

    /**
     * 统计数据数量
     *
     * @return
     */
    int count();
}