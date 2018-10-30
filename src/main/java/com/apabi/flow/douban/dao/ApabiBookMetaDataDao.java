package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author pipi
 * @date 2018/10/26 11:12
 * @description
 */
@Mapper
@Repository
public interface ApabiBookMetaDataDao {
    ApabiBookMetaData findById(String metaId);
    void delete(String metaId);
    void insert(ApabiBookMetaData apabiBookMetaData);
    void update(ApabiBookMetaData apabiBookMetaData);
    Page<String> findIsbnByPageWithoutCrawledNlc();
    void updateNlcMarcId(@Param("nlibraryId") String nlibraryId,@Param("isbn") String isbn);
    int findIsbnCount();
}