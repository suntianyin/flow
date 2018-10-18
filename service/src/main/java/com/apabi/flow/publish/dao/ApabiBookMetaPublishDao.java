package com.apabi.flow.publish.dao;

import com.apabi.flow.publish.model.ApabiBookMetaPublish2;
import org.springframework.stereotype.Repository;

/**
 * @author pipi
 * @date 2018/8/9 10:12
 * @description
 */
@Repository
public interface ApabiBookMetaPublishDao {
    ApabiBookMetaPublish2 findApabiBookMetaPublishByMetaIdIs(String metaId);
    void saveAndFlush(ApabiBookMetaPublish2 apabiBookMetaPublish2);
}
