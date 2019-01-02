package com.apabi.flow.match_data.dao;

import com.apabi.flow.match_data.model.ApabiBookMetaDoubanMatcher;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author pipi
 * @Date 2019-1-2 13:47
 **/
@Mapper
@Repository
public interface ApabiBookMetaDoubanMatcherDao {
    int delete(String metaId);

    int insert(ApabiBookMetaDoubanMatcher record);

    ApabiBookMetaDoubanMatcher findById(String metaId);

    List<ApabiBookMetaDoubanMatcher> findAll();

    int update(ApabiBookMetaDoubanMatcher record);

    int count();
}
