package com.apabi.flow.isbn.dao;

import com.apabi.flow.isbn.model.IsbnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pipi on 2018/8/23.
 */
@Repository
@Mapper()
public interface IsbnDao {
    @Select("SELECT ISBN,ISBN10,ISBN13,METAID,TITLE,CREATOR,PUBLISHER,HASCEBX,HASFLOW FROM APABI_BOOK_METADATA where ISBN = #{isbn}")
    @ResultMap({"isbnEntityMapper"})
    List<IsbnEntity> findIsbnEntityByIsbn(String isbn);

    @Select("SELECT ISBN,ISBN10,ISBN13,METAID,TITLE,CREATOR,PUBLISHER,HASCEBX,HASFLOW FROM APABI_BOOK_METADATA where ISBN10 = #{isbn10}")
    @ResultMap({"isbnEntityMapper"})
    List<IsbnEntity> findIsbnEntityByIsbn10(String isbn);

    @Select("SELECT ISBN,ISBN10,ISBN13,METAID,TITLE,CREATOR,PUBLISHER,HASCEBX,HASFLOW FROM APABI_BOOK_METADATA where ISBN13 = #{isbn13}")
    @ResultMap({"isbnEntityMapper"})
    List<IsbnEntity> findIsbnEntityByIsbn13(String isbn);
}
