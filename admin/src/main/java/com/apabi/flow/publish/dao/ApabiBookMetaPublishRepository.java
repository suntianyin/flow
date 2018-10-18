package com.apabi.flow.publish.dao;

import com.apabi.flow.publish.model.ApabiBookMetaPublish;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author pipi
 * @date 2018/8/9 10:24
 * @description
 */
public interface ApabiBookMetaPublishRepository extends JpaRepository<ApabiBookMetaPublish,String> {
    ApabiBookMetaPublish findApabiBookMetaPublishByMetaIdIs(String metaId);
}
