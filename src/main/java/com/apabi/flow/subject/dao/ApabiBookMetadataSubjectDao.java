package com.apabi.flow.subject.dao;

import com.apabi.flow.subject.model.ApabiBookMetadataSubject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-12 17:52
 **/
@Mapper
@Repository
public interface ApabiBookMetadataSubjectDao {
    int delete(String id);

    int insert(ApabiBookMetadataSubject apabiBookMetadataSubject);

    ApabiBookMetadataSubject findById(String id);

    int update(ApabiBookMetadataSubject apabiBookMetadataSubject);

    int count();
}
