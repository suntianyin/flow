package com.apabi.flow.publish.service;

import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 *
 * @author pipi
 * @date 2018/8/15
 */
public interface ApabiBookMetaPublishService {
    /**
     * 分页查询apabi_book_metadata_temp数据
     *
     * @param queryMap
     * @return
     */
    Page<ApabiBookMetaDataTemp> queryPage(Map<String, Object> queryMap);

    /**
     * 根据metaid查询apabi_book_metadata_temp数据
     *
     * @param metaId
     * @return
     */
    ApabiBookMetaDataTemp findApabiBookMetaTempPublish(String metaId);

    /**
     * 根据metaId查询apabi_book_metadata数据
     *
     * @param metaId
     * @return
     */
    ApabiBookMetaData findApabiBookMetaPublish(String metaId);

    /**
     * 将apabi_book_metadata_temp发布到apabi_book_metadata
     *
     * @param metaId
     */
    void publishApabiBookMetaTemp(String metaId);

    void updateApabiBookMetaTemp(ApabiBookMetaDataTemp apabiBookMetaDataTemp);
}
