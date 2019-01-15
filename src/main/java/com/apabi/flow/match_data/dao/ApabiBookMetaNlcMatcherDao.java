package com.apabi.flow.match_data.dao;

import com.apabi.flow.match_data.model.ApabiBookMetaNlcMatcher;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2019-1-7 17:29
 **/
@Mapper
@Repository
public interface ApabiBookMetaNlcMatcherDao {

    List<ApabiBookMetaNlcMatcher> findAll();

    int delete(String metaId);

    int insert(ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher);

    ApabiBookMetaNlcMatcher findById(String metaId);

    int update(ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher);

    int count();

}
