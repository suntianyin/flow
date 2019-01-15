package com.apabi.flow.match_data.dao;

import com.apabi.flow.match_data.model.ApabiBookMetaDoubanChecker;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2019-1-9 11:11
 **/
@Mapper
@Repository
public interface ApabiBookMetaDoubanCheckerDao {
    int count();

    int delete(String doubanId);

    int insert(ApabiBookMetaDoubanChecker apabiBookMetaDoubanChecker);

    ApabiBookMetaDoubanChecker findById(String doubanId);

    int update(ApabiBookMetaDoubanChecker apabiBookMetaDoubanChecker);
}
