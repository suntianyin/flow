package com.apabi.flow.auth.service.impl;

import com.apabi.flow.auth.dao.BookListMapper;
import com.apabi.flow.auth.model.BookList;
import com.apabi.flow.auth.service.BookListService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/12/07 15:03
 * @description:
 */
@Service
public class BookListServiceImpl implements BookListService {

    @Autowired
    BookListMapper bookListMapper;

    @Override
    public Page<BookList> listBookList(Map<String, Object> paramsMap) {
        return bookListMapper.listBookList(paramsMap);
    }

    @Override
    public int add(BookList bookList) {
        return bookListMapper.insert(bookList);
    }

    @Override
    public BookList selectByPrimaryKey(String caid) {
        return bookListMapper.selectByPrimaryKey(caid);
    }

    @Override
    public int updateByPrimaryKeySelective(BookList record) {
        return bookListMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPrimaryKey(String caid) {
        return bookListMapper.deleteByPrimaryKey(caid);
    }

    @Override
    public int saveAgreementFileNameAndPath(String id, String filePath) {
//        CopyrightAgreement copyrightAgreement = copyrightAgreementMapper.selectByPrimaryKey(caid);
//        if(StringUtils.isNotBlank(copyrightAgreement.getAgreementFileName())||StringUtils.isNotBlank(copyrightAgreement.getAgreementFilePath())){
//            return -1;
//        }
        String[] split = filePath.split("/");
        String fileName=split[split.length-1];
        int i = bookListMapper.updateFileNameAndFilePathById(id, fileName, filePath);
        return i;
    }

    @Override
    public List<BookList> findAll() {
        return bookListMapper.findAll();
    }
}
