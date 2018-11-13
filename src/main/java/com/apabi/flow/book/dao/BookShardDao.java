package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookShard;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/29 10:34
 * @description
 */
@Repository
@Mapper
public interface BookShardDao {

    //根据id获取分组内容
    BookShard findBookShardByComId(String comId);

    //根据metaId获取所有分组信息
    List<BookShard> findAllBookShard(String metaId);

    //插入分组数据
    int insertBookShard(BookShard bookShard);

    //更新图书章节内容
    int updateBookShard(BookShard bookShard);

    //删除章节内容
    int deleteAllBookShard(String metaId);
}
