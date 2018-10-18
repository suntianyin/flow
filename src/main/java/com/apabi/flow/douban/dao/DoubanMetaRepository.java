package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.DoubanMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by pipi on 2018/8/8.
 */
public interface DoubanMetaRepository extends JpaRepository<DoubanMeta,String> {
    //根据豆瓣库中ISBN13，获取豆瓣数据内容
    DoubanMeta findDoubanMetaByIsbn13Is(String isbn13);

    // 由于数据库中的的数据存在重复的isbn13，返回DoubanMeta列表
    List<DoubanMeta> findDoubanMetasByIsbn13Is(String isbn13);
}
