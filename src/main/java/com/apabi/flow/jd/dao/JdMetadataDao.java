package com.apabi.flow.jd.dao;

import com.apabi.flow.jd.model.JdMetadata;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-04 10:47
 **/
@Mapper
@Repository
public interface JdMetadataDao {
    List<JdMetadata> findAll();

    int delete(String jdItemId);

    int insert(JdMetadata jdMetadata);

    JdMetadata findById(String jdItemId);

    int update(JdMetadata jdMetadata);

    void updateHasCrawled(String url);
}