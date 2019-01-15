package com.apabi.flow.match_data.dao;

import com.apabi.flow.match_data.model.ApabiBookMetaNlcChecker;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author pipi
 * @Date 2019-1-14 17:54
 **/
@Mapper
@Repository
public interface ApabiBookMetaNlcCheckerDao {
    int delete(String nlibraryId);

    int insert(ApabiBookMetaNlcChecker apabiBookMetaNlcChecker);

    ApabiBookMetaNlcChecker findById(String nlibraryId);

    int update(ApabiBookMetaNlcChecker apabiBookMetaNlcChecker);
}
