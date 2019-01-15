package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.util.Isbn13ToIsbnUtil;
import com.apabi.flow.match_data.dao.ApabiBookMetaNlcCheckerDao;
import com.apabi.flow.match_data.model.ApabiBookMetaNlcChecker;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2019-1-9 11:16
 **/
@RestController
@RequestMapping("apabiNlcChecker")
public class ApabiBookMetaNlcCheckerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaNlcCheckerService.class);
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaNlcCheckerDao apabiBookMetaNlcCheckerDao;

    /**
     * 如果作者和出版社都不一样，则认为douban_meta和apabi_book_meta数据不是同一本书
     * 进入把这种数据写入到apabi_book_meta_douban_checker表中
     *
     * @return
     */
    @RequestMapping("check")
    public String check() {
        int count = apabiBookMetaDataDao.countHasNLibraryId();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findApabiBookMetaDataWithNlibraryId();
            for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                try {
                    NlcBookMarc nlcBookMarc = nlcBookMarcDao.findByNlcMarcId(apabiBookMetaData.getNlibraryId());
                    if (!nlcBookMarc.getTitle().trim().equals(apabiBookMetaData.getTitle().trim()) && !nlcBookMarc.getAuthor().trim().equals(apabiBookMetaData.getCreator().trim())) {
                        ApabiBookMetaNlcChecker apabiBookMetaNlcChecker = new ApabiBookMetaNlcChecker();
                        apabiBookMetaNlcChecker.setNlibraryId(nlcBookMarc.getNlcMarcId().trim());
                        apabiBookMetaNlcChecker.setMetaId(apabiBookMetaData.getMetaId().trim());
                        apabiBookMetaNlcChecker.setNlcTitle(nlcBookMarc.getTitle().trim());
                        apabiBookMetaNlcChecker.setApabiMetaTitle(apabiBookMetaData.getTitle().trim());
                        apabiBookMetaNlcChecker.setNlcAuthor(nlcBookMarc.getAuthor().trim());
                        apabiBookMetaNlcChecker.setApabiMetaAuthor(apabiBookMetaData.getCreator().trim());
                        apabiBookMetaNlcChecker.setNlcPublisher(nlcBookMarc.getPublisher().trim());
                        apabiBookMetaNlcChecker.setApabiMetaPublisher(apabiBookMetaData.getPublisher().trim());
                        apabiBookMetaNlcCheckerDao.insert(apabiBookMetaNlcChecker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "success";
    }

    /**
     * 根据isbn13查询，重新更新apabi_book_metadata中doubanId更新失败的数据
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("checkIsbn13")
    @ResponseBody
    public String checkIsbn13() {
        List<String> isbn13List = apabiBookMetaDataDao.findIsbn13WithoutNlibraryId();
        for (String isbn13 : isbn13List) {
            if (StringUtils.isNotEmpty(isbn13)) {
                String isbn = Isbn13ToIsbnUtil.transform(isbn13);
                List<NlcBookMarc> nlcBookMarcList = nlcBookMarcDao.findByIsbn(isbn);
                List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn13(isbn13);
                if (nlcBookMarcList != null && nlcBookMarcList.size() == 1 && apabiBookMetaDataList.size() == 1) {
                    try {
                        String nlcMarcId = nlcBookMarcList.get(0).getNlcMarcId();
                        ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                        apabiBookMetaData.setNlibraryId(nlcMarcId);
                        apabiBookMetaDataDao.update(apabiBookMetaData);
                        System.out.println(apabiBookMetaData.getIsbn13());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "success";
    }
}