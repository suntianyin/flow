package com.apabi.flow.match_data.dao;


import com.apabi.flow.match_data.model.ApabiBookMetaNlcMatched;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ApabiBookMetaNlcMatchedDao {
    int count();

    int delete(String metaId);

    int insert(ApabiBookMetaNlcMatched record);

    ApabiBookMetaNlcMatched findById(String id);

    int update(ApabiBookMetaNlcMatched record);

    Page<ApabiBookMetaNlcMatched> findByPage();

    List<ApabiBookMetaNlcMatched> findAll();
}