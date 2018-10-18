package com.apabi.flow.publish.dao;

import com.apabi.flow.publish.model.ApabiBookMetaTempPublish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author pipi
 * @date 2018/8/9 10:24
 * @description
 */
public interface ApabiBookMetaTempPublishRepository extends JpaRepository<ApabiBookMetaTempPublish,String> ,PagingAndSortingRepository<ApabiBookMetaTempPublish, String>, JpaSpecificationExecutor<ApabiBookMetaTempPublish> {
    ApabiBookMetaTempPublish findApabiBookMetaTempPublishByMetaIdIs(String metaId);
}
