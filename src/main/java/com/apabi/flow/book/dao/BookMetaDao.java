package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.model.BookMetaBatch;
import com.apabi.flow.book.model.BookMetaVo;
import com.apabi.flow.book.model.EpubookMeta;
import com.apabi.flow.bookSearch.model.BookSearchModel;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/27 17:23
 * @description
 */
@Repository
@Mapper
public interface BookMetaDao {

    //获取图书元数据
    BookMeta findBookMetaById(String metaid);

    //获取图书元数据
    BookMetaVo findBookMetaVoById(String metaid);

    //通过isbn获取图书元数据
    BookMetaVo findBookMetaVoByIsbn(String isbn);

    //获取图书元数据
    List<BookMetaBatch> findBookMetaBatchById(String metaId);

    //通过isbn获取图书元数据
    List<BookMetaBatch> findBookMetaBatchByIsbn(String isbn);

    //通过isbn获取图书元数据
    List<BookMetaBatch> findBookMetaBatchByIsbn13(String isbn);

    //获取图书元数据
    EpubookMeta findEpubookMetaById(String metaid);

    //获取图书目录
    String findCataRowsById(String metaid);

    //保存epub图书元数据
    int updateEpubookMeta(EpubookMeta epubookMeta);

    //更新图书元数据
    int updateBookMetaById(BookMeta bookMeta);

    //更新图书元数据
    int updateBookMetaVo(BookMetaVo bookMetaVo);

    //保存图书元数据
    int insertBookMeta(BookMeta bookMeta);

    //查询图书元数据条数
    int countBookMetaVoById(String metaid);

    //获取最大drid
    int getMaxDrid();

    //分页查询
    Page<BookMetaVo> findBookMetaVoByPage(Map<String, Object> queryMap);

    /**
     * 根据isbn查询 BookMeta
     *
     * @param isbn
     * @return
     */
    List<BookMeta> listBookMetaByIsbn(String isbn);

    /**
     * 根据isbn13查询 BookMeta
     *
     * @param isbn13
     * @return
     */
    List<BookMeta> listBookMetaByIsbn13(String isbn13);

}
