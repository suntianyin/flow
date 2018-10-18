package com.apabi.flow.publish.dao;

import com.apabi.flow.publish.model.PublishResult;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface PublishResultDao {
    PublishResult findPublishResultById(String id);
    void insertPublishResult(PublishResult publishResult);
    void deletePublishResultById(String id);
    void updatePublishResult(PublishResult publishResult);
    Page<PublishResult> findPublishResultByPage(Map<String, String[]> params);
}