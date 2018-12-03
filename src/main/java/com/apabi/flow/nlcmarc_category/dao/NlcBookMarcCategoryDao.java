package com.apabi.flow.nlcmarc_category.dao;

import com.apabi.flow.nlcmarc_category.model.NlcBookMarcCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/11/21.
 */
@Repository
@Mapper
public interface NlcBookMarcCategoryDao {
    NlcBookMarcCategory findById(String id);

    void insert(NlcBookMarcCategory nlcBookMarcCategory);

    void delete(String id);

    void update(NlcBookMarcCategory nlcBookMarcCategory);

    List<String> findAllIdListByPageNum();

    List<NlcBookMarcCategory> findCategoryMoreThan2PagesAndNotCrawled();
}
