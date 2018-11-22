package com.apabi.flow.book.service;

import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.model.BookChapterSum;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/7/31 14:12
 * @description
 */
public interface BookChapterService {

    //根据图书id和章节号，获取章节内容
    BookChapter selectChapterById(String metaid, Integer chapterNum);

    //添加图书章节内容
    int insertBookChapter(List<BookChapter> bookChapterList);

    //删除metaid下的所有内容
    int deleteAllChapterByMetaid(String metaid);

    //获取图书章节及分组信息
    List<BookChapterSum> selectBookChapterSum(String metaid);

    //更新图书章节内容
    int updateBookChapter(BookChapter bookChapter) ;

    //流式图书内容乱码检测
    void detectBookChapter();

}
