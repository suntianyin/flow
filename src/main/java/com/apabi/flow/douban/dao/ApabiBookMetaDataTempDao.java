package com.apabi.flow.douban.dao;


import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author pipi
 * @date 2018/10/30 13:12
 * @description
 */
@Mapper
@Repository
public interface ApabiBookMetaDataTempDao {
    ApabiBookMetaDataTemp findById(String metaId);
    List<ApabiBookMetaDataTemp> findByIsbn13(String isbn13);
    List<ApabiBookMetaDataTemp> findByIsbn(String isbn);
    void delete(String metaId);
    void insert(ApabiBookMetaDataTemp apabiBookMetaDataTemp);
    void update(ApabiBookMetaDataTemp apabiBookMetaDataTemp);
}