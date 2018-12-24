package com.apabi.flow.xinhuashudaun.dao;

import com.apabi.flow.xinhuashudaun.model.XinhuashudianMetadata;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2018-12-14 14:16
 **/
@Mapper
@Repository
public interface XinhuashudianMetadataDao {
    int delete(String itemId);

    int insert(XinhuashudianMetadata xinhuashudianMetadata);

    XinhuashudianMetadata findById(String itemId);

    int update(XinhuashudianMetadata record);

    int count();
}
