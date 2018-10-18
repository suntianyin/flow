package com.apabi.flow.book.service;

import com.apabi.flow.book.entity.MongoBookChapter;
import com.apabi.flow.book.entity.MongoBookMeta;
import com.apabi.flow.book.entity.MongoBookShard;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/23 16:17
 * @description
 */
public interface MongoBookService {

    //根据图书id和章节号，获取章节内容
    MongoBookChapter findBookChapterByComId(String comId);

    //获取所有章节
    List<MongoBookChapter> findBookChapterById(String id);

    //获取所有章节分组
    List<MongoBookShard> findBookShardById(String id);

    //获取图书元数据
    MongoBookMeta findBookMetaByIsbnAndTitle(String isbn, String title);

    //获取图书元数据
    MongoBookMeta findBookMetaByIsbn(String isbn);

    //获取图书元数据
    List<MongoBookMeta> findAllBookByIsbn(String isbn);

    //获取图书元数据
    MongoBookMeta findBookMetaByTitle(String title);

    //获取图书元数据
    MongoBookMeta findBookMetaById(String id);

    //根据图书id获取元数据
    Long countBookMetaById(String metaid);

    //存储图书元数据
    int saveBookMeta(MongoBookMeta bookMeta);

    //更新图书元数据
    int updateBookMeta(MongoBookMeta bookMeta);

    //存储章节内容
    int saveBookChapter(List<MongoBookChapter> chapters);

    //存储或更新章节内容
    int updateBookChapter(List<MongoBookChapter> chapters);

    //存储或更新章节内容分组
    int updateBookShard(List<MongoBookShard> shards);
}
