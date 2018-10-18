package com.apabi.flow.book.service;

import com.apabi.flow.book.model.*;
import org.springframework.data.domain.Page;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/1 10:48
 * @description
 */
public interface BookMetaService {

    //根据图书metaid，获取图书元数据
    BookMetaVo selectBookMetaById(String metaid);

    //根据图书metaid，获取图书元数据
    EpubookMeta selectEpubookMetaById(String metaid);

    //根据图书metaid，获取图书元数据
    BookMeta selectBookMetaDetailById(String metaid);

    //保存图书元数据
    int saveBookMeta(BookMeta bookMeta);

    //保存epub图书元数据
    int saveEpubookMeta(EpubookMeta epubookMeta);

    //删除图书元数据
    int deleteBookMeta(String metaid);

    //删除图书元数据内容
    int deleteBookChapter(String metaid);

    //查询图书是否存在
    int countBookMetaById(String metaid);

    //获取目录信息
    List<BookCataRows> getCataRowsById(String metaid);

    //解析xml数据，并保存
    int saveXmlBookMeta(String path) throws Exception;

    //分页查询
    Page<BookMetaVo> queryPage(Map queryMap, int start, int size);

    //获取epub文件的图书元数据
    List<BookMetaBatch> getBookMetaBatch(String dir);

    //将指定图书存入爱读爱看
    int insertBookMeta2Mongo(String ids, Map<String, String> conds) throws ParseException;
}