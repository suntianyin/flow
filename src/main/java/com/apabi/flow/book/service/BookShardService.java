package com.apabi.flow.book.service;

import com.apabi.flow.book.model.BookShard;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/1 14:13
 * @description
 */
public interface BookShardService {

    //根据图书id、章节号和分组号，获取章节内容
    BookShard selectShardById(String metaid, Integer chapterNum, Integer index);

    //添加图书章节分组内容
    int insertBookShard(List<BookShard> bookShardList);

    //删除图书metaid下的所有章节分组内容
    int deleteAllShardByMetaid(String metaid);
}
