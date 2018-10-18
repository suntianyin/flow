package com.apabi.flow.douban.dao;

import com.apabi.flow.douban.model.ApabiBookMetaTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author pipi
 * @date 2018/8/9 10:24
 * @description
 */
public interface ApabiBookMetaTempRepository extends JpaRepository<ApabiBookMetaTemp,String> {
    List<ApabiBookMetaTemp> findApabiBookMetaTempByIsbn13Is(String isbn13);

}
