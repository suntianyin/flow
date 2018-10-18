package com.apabi.flow.publish.dao;

import com.apabi.flow.publish.model.ApabiBookMetaTempPublish2;
import org.springframework.stereotype.Repository;

/**
 * @author pipi
 * @date 2018/8/9 10:12
 * @description
 */
@Repository
public interface ApabiBookMetaTempPublishDao {
    ApabiBookMetaTempPublish2 findApabiBookMetaTempPublishByMetaIdIs(String metaId);
    void saveAndFlush(ApabiBookMetaTempPublish2 apabiBookMetaTempPublish2);
}
