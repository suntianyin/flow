package com.apabi.flow.bookSearch.service;

import com.apabi.flow.bookSearch.model.BookSearchModel;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 * Created by pipi on 2018/9/10.
 */
public interface BookSearchModelService {
    BookSearchModel findBookSearchByMetaId(String metaId);
    Page<BookSearchModel> findBookSearchByPage(Map<String,String> params);
}
