package com.apabi.flow.nlcmarc.service.impl;

import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/13 18:30
 **/
@Service
public class NlcBookMarcServiceImpl implements NlcBookMarcService {
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;

    @Override
    public NlcBookMarc findNlcBookMarcByNlcBookMarcId(String nlcBookMarcId) {
        NlcBookMarc nlcBookMarc = null;
        if (!StringUtils.isEmpty(nlcBookMarcId)) {
            nlcBookMarc = nlcBookMarcDao.findByNlcMarcId(nlcBookMarcId);
        }
        return nlcBookMarc;
    }

    @Override
    public void updateNlcBookMarc(NlcBookMarc nlcBookMarc) {
        if (nlcBookMarc != null) {
            nlcBookMarcDao.updateNlcBookMarc(nlcBookMarc);
        }
    }

    @Override
    public void insertNlcBookMarc(NlcBookMarc nlcBookMarc) {
        if (nlcBookMarc != null) {
            nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
        }
    }

    @Override
    public List<NlcBookMarc> parseNlcBookMarc(String filePath, String charset) throws IOException {
        List<NlcBookMarc> nlcBookMarcList = new ArrayList<>();
        if (!StringUtils.isEmpty(filePath)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));
            String line = null;
            while((line = reader.readLine())!=null){
                NlcBookMarc nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(line);
                nlcBookMarc.setIsoFilePath(filePath);
                nlcBookMarcList.add(nlcBookMarc);
            }
            reader.close();
        }
        return nlcBookMarcList;
    }

    @Override
    public Page<NlcBookMarc> findByPage() {
        Page<NlcBookMarc> page = nlcBookMarcDao.findByPage();
        return page;
    }

    @Override
    public int getTotalCount() {
        return nlcBookMarcDao.getTotalCount();
    }
}
