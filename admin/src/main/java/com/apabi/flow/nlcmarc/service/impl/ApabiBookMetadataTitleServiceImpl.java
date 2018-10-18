package com.apabi.flow.nlcmarc.service.impl;

import com.apabi.flow.nlcmarc.dao.ApabiBookMetadataTitleDao;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataTitleService;
import com.apabi.flow.nlcmarc.util.ParseMarcTitleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author pipi
 * @Date 2018/10/10 11:20
 **/
@Service
public class ApabiBookMetadataTitleServiceImpl implements ApabiBookMetadataTitleService {
    private Logger logger = LoggerFactory.getLogger(ApabiBookMetadataTitleServiceImpl.class);
    @Autowired
    private ApabiBookMetadataTitleDao apabiBookMetadataTitleDao;

    @Override
    public void insert(ApabiBookMetadataTitle apabiBookMetadataTitle) {
        try {
            apabiBookMetadataTitleDao.insert(apabiBookMetadataTitle);

        } catch (Exception e) {
            logger.error(apabiBookMetadataTitle + "插入失败");
        }
    }

    @Override
    public ApabiBookMetadataTitle findById(String id) {
        return apabiBookMetadataTitleDao.findById(id);
    }

    @Override
    public ApabiBookMetadataTitle parseTitle(String info) {
        ApabiBookMetadataTitle apabiBookMetadataTitle = null;
        try {
            apabiBookMetadataTitle = ParseMarcTitleUtil.parseMarcTitle(info);
            logger.info(apabiBookMetadataTitle + "处理题名信息成功...");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(apabiBookMetadataTitle + "处理题名信息失败...原因为：" + e.getMessage());
        }
        return apabiBookMetadataTitle;
    }

}
