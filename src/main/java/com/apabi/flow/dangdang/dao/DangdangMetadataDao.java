package com.apabi.flow.dangdang.dao;

import com.apabi.flow.dangdang.model.DangdangMetadata;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-7 9:18
 **/
@Mapper
@Repository
public interface DangdangMetadataDao {

    int delete(String pid);

    int insert(DangdangMetadata dangdangMetadata);

    DangdangMetadata findById(String pid);

    int update(DangdangMetadata dangdangMetadata);
}
