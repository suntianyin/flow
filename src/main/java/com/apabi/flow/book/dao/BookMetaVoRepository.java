package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookMetaVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author guanpp
 * @date 2018/8/1 17:02
 * @description
 */
public interface BookMetaVoRepository extends JpaRepository<BookMetaVo, String>, PagingAndSortingRepository<BookMetaVo, String>, JpaSpecificationExecutor<BookMetaVo> {

    //根据图书metaid，获取图书元数据
    BookMetaVo findBookMetaVoByMetaidIs(String metaid);

    //根据图书metaid，获取图书目录数据
    @Query(value = "SELECT * FROM APABI_BOOK_METADATA a WHERE a.METAID =?1", nativeQuery = true)
    BookMetaVo findByMetaidIs(String metaid);

    //查询条数
    int countByMetaidIs(String metaid);
}
