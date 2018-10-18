package com.apabi.flow.cleanData.dao;

import com.apabi.flow.cleanData.model.CleanData;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/9/19.
 */
@Repository
@Mapper
public interface CleanDataDao {
    void saveAndFlush(CleanData cleanData);
    List<CleanData> findApabiBookMetaTempPublishByMetaIdIs(String metaId);
    void update(CleanData cleanData);
    Page<String> findMetaIdsByPage();
    Page<String> findMetaIdsByPageWithoutClean();
    void updateIssuedDateAndIsbn(@Param("issuedDate") String issuedDate, @Param("isbn") String isbn, @Param("metaId") String metaId);
    Integer getTotalCount();
    Integer getTotalCountWithoutClean();
}
