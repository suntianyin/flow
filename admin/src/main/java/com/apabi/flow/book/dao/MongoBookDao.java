package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.entity.MongoBookChapter;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/23 14:27
 * @description
 */
public interface MongoBookDao {
    //根据图书id和章节号，获取章节内容
    MongoBookChapter findBookChapterByComId(String comId);

    //查询图书metaid下的所有章节内容
    List<BookChapter> findAllBookChapter(String metaid);
}
