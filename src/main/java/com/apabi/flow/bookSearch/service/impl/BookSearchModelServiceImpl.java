package com.apabi.flow.bookSearch.service.impl;

import com.apabi.flow.bookSearch.dao.BookSearchModelDao;
import com.apabi.flow.bookSearch.model.BookSearchModel;
import com.apabi.flow.bookSearch.service.BookSearchModelService;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/9/10 14:02
 **/
@Service
public class BookSearchModelServiceImpl implements BookSearchModelService {
    @Autowired
    private BookSearchModelDao bookSearchModelDao;

    @Override
    public BookSearchModel findBookSearchByMetaId(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            BookSearchModel bookSearchModel = bookSearchModelDao.findBookSearchByMetaId(metaId);
            return bookSearchModel;
        }
        return null;
    }

    @Override
    public Page<BookSearchModel> findBookSearchByPage(Map<String, String> params) {
        return bookSearchModelDao.findBookSearchByPage(params);
    }

    @Override
    public int count() {
        return bookSearchModelDao.count();
    }
}
