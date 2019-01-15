package com.apabi.flow.nlcmarc.dao;

import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NlcBookMarcDao {
    NlcBookMarc findByNlcMarcId(String nlcMarcId);

    void deleteByNlcMarcId(String nlcMarcId);

    void insertNlcMarc(NlcBookMarc nlcBookMarc);

    void updateNlcBookMarc(NlcBookMarc nlcBookMarc);

    Page<NlcBookMarc> findByPage();

    int getTotalCount();

    List<NlcBookMarc> findByIsbn(String isbn);
}