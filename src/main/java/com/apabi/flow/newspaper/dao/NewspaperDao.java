package com.apabi.flow.newspaper.dao;

import com.apabi.flow.newspaper.model.Newspaper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NewspaperDao {
    Newspaper findByUrl(String url);
    void delete(String url);
    void insert(Newspaper newspaper);
    void update(Newspaper newspaper);
    Page<String> findNoHtmlContentUrlsByPage();
    Integer countNoHtmlContent();
}