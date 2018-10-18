package com.apabi.flow.bookSearch.dao;

import com.apabi.flow.bookSearch.model.BookSearchModel;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/9/10 13:49
 **/
@Mapper
@Repository
public interface BookSearchModelDao {
    BookSearchModel findBookSearchByMetaId(String metaId);
    Page<BookSearchModel> findBookSearchByPage(Map<String,String> params);
}
