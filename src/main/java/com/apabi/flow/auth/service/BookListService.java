package com.apabi.flow.auth.service;

import com.apabi.flow.auth.model.BookList;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/12/07 15:02
 * @description:
 */

public interface BookListService {
    Page<BookList> listBookList(Map<String, Object> paramsMap);

    int add(BookList copyrightAgreement);

    BookList selectByPrimaryKey(String caid);

    int updateByPrimaryKeySelective(BookList record);

    int deleteByPrimaryKey(String caid);

    int saveAgreementFileNameAndPath(String caid, String filePath);

    List<BookList> findAll();
}
