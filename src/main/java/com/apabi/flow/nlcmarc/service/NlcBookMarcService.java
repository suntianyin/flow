package com.apabi.flow.nlcmarc.service;

import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;

import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/13 18:30
 **/
public interface NlcBookMarcService {
    NlcBookMarc findNlcBookMarcByNlcBookMarcId(String nlcBookMarcId);
    void updateNlcBookMarc(NlcBookMarc nlcBookMarc);
    void insertNlcBookMarc(NlcBookMarc nlcBookMarc);
    List<NlcBookMarc> parseNlcBookMarc(String filePath, String charset) throws IOException;
    Page<NlcBookMarc> findByPage();
    int getTotalCount();
}
