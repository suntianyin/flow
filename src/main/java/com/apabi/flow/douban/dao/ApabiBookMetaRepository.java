package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.ApabiBookMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author pipi
 * @date 2018/8/9 10:24
 * @description
 */
public interface ApabiBookMetaRepository extends JpaRepository<ApabiBookMeta,String> {
    //根据豆瓣库中ISBN13，获取豆瓣数据内容
    ApabiBookMeta findApabiBookMetaByIsbn13Is(String isbn13);

    List<ApabiBookMeta> findApabiBookMetasByIsbn13Is(String isbn13);
}
