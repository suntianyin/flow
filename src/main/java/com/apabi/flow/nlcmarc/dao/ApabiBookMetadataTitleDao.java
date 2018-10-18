package com.apabi.flow.nlcmarc.dao;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by pipi on 2018/10/10.
 */
@Mapper
@Repository
public interface ApabiBookMetadataTitleDao {
    ApabiBookMetadataTitle findById(String id);
    void delete(String id);
    void insert(ApabiBookMetadataTitle apabiBookMetadataTitle);
    void update(ApabiBookMetadataTitle apabiBookMetadataTitle);
}
