package com.apabi.flow.jd.dao;

import com.apabi.flow.jd.model.JdMetadata;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018-12-04 10:47
 **/
@Mapper
@Repository
public interface JdMetadataDao {
    int count();

    Page<JdMetadata> findShouldCrawl();

    List<JdMetadata> findAll();

    int delete(String jdItemId);

    int insert(JdMetadata jdMetadata);

    JdMetadata findById(String jdItemId);

    int update(JdMetadata jdMetadata);

    void updateHasCrawled(String url);

    Page<String> findAllIsbn13ByPage();

    Page<JdMetadata> findByPage(Map<String,String> params);

    JdMetadata findByIsbn13(String isbn13);
}