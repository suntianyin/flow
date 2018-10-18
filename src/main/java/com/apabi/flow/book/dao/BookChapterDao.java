package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.model.BookChapterSum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/21 14:27
 * @description
 */
@Repository
@Mapper
public interface BookChapterDao {
    //根据图书id和章节号，获取章节内容
    BookChapter findBookChapterByComId(String comId);

    //查询图书metaid下的所有章节内容
    List<BookChapter> findAllBookChapter(String metaid);

    //查询图书metaid下的所有章节内容
    List<BookChapterSum> findAllBookChapterSum(String metaid);

    //保存图书章节内容
    int insertBookChapter(BookChapter bookChapter);

    //更新图书章节内容
    int updateBookChapter(BookChapter bookChapter);
}
