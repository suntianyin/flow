package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    Page<ApabiBookMetaData> findApabiBookMetaDataWithDoubanId();
    Page<ApabiBookMetaData> findApabiBookMetaDataWithNlibraryId();
    int countHasDoubanId();
    int countHasNLibraryId();
    void updateNlcMarcId(@Param("nlibraryId") String nlibraryId,@Param("isbn") String isbn);
    int findIsbnCount();
    List<ApabiBookMetaData> findByIsbn13(String isbn13);
    List<ApabiBookMetaData> findByIsbn10(String isbn10);
    List<String> findIsbn13WithoutDoubanId();
    List<String> findIsbn10WithoutDoubanId();
    List<String> findIsbn13WithoutNlibraryId();
    List<String> findIsbn10WithoutNlibraryId();
}