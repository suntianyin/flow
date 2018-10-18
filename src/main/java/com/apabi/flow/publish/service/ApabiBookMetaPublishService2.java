package com.apabi.flow.publish.service;

import com.apabi.flow.publish.model.ApabiBookMetaPublish2;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish2;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Created by pipi on 2018/8/15.
 */
public interface ApabiBookMetaPublishService2 {
    // 分页查询
    Page<ApabiBookMetaTempPublish2> queryPage(Map queryMap, int start, int size);
    // 根据metaId,isbn13,isbn10,isbn查询ApabiBookMetaTempPublish
    ApabiBookMetaTempPublish2 findApabiBookMetaTempPublish(String metaId);
    // 根据metaId,isbn13,isbn10,isbn查询ApabiBookMetaPublish
    ApabiBookMetaPublish2 findApabiBookMetaPublish(String metaId);
    // 发布apabiBookMetaTempPublish
    void publishApabiBookMetaTemp(String metaId);
    // 将修改的apabiBookMetaTemp更新到数据库
    void updateApabiBookMetaTemp(ApabiBookMetaTempPublish2 apabiBookMetaTempPublish);

    void addApabiBookMetaTemp(ApabiBookMetaPublish2 apabiBookMetaPublish2);
}
