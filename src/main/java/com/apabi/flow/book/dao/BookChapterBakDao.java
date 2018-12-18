package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.model.BookChapterSum;
import com.apabi.flow.book.model.BookChapterVo;
import com.github.pagehelper.Page;
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
public interface BookChapterBakDao {
    //根据图书id和章节号，获取章节内容
    BookChapterVo findBookChapterVoByComId(String comId);

    //获取章节简要信息
    BookChapterSum findChapterByComId(String comId);

    //查询图书metaid下的所有章节内容
    List<BookChapterVo> findAllBookChapterVo(String metaId);

    //查询图书metaid下的所有章节内容
    List<BookChapterSum> findAllBookChapterSum(String metaId);

    //保存图书章节内容
    int insertBookChapterVo(BookChapterVo bookChapter);

    //更新图书章节内容
    int updateBookChapterVo(BookChapterVo bookChapter);

    //删除章节内容
    int deleteAllBookChapterVo(String metaId);

    //获取章节表的总条数
    int getTotal();

    //分页查询
    Page<BookChapterVo> findBookChapterVoByPage();
}
