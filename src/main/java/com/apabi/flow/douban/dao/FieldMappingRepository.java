package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.FieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by pipi on 2018/8/8.
 */
public interface FieldMappingRepository extends JpaRepository<FieldMapping,String> {
    List<FieldMapping> findAll();
}
