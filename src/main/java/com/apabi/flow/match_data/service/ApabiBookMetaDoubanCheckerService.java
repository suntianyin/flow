package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.match_data.dao.ApabiBookMetaDoubanCheckerDao;
import com.apabi.flow.match_data.model.ApabiBookMetaDoubanChecker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
@RequestMapping("apabiDoubanChecker")
public class ApabiBookMetaDoubanCheckerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaDoubanCheckerService.class);
    @Autowired
    private DoubanMetaDao doubanMetaDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaDoubanCheckerDao apabiBookMetaDoubanCheckerDao;

    /**
     * 如果作者和标题都不一样，则认为douban_meta和apabi_book_meta数据不是同一本书
     * 进入把这种数据写入到apabi_book_meta_douban_checker表中
     *
     * @return
     */
    @RequestMapping("check")
    public String check() {
        int count = apabiBookMetaDataDao.countHasDoubanId();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findApabiBookMetaDataWithDoubanId();
            for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                try {
                    DoubanMeta doubanMeta = doubanMetaDao.findById(apabiBookMetaData.getDoubanId());
                    if (!doubanMeta.getTitle().trim().equals(apabiBookMetaData.getTitle().trim()) && !doubanMeta.getAuthor().trim().equals(apabiBookMetaData.getCreator().trim())) {
                        ApabiBookMetaDoubanChecker apabiBookMetaDoubanChecker = new ApabiBookMetaDoubanChecker();
                        apabiBookMetaDoubanChecker.setDoubanId(doubanMeta.getDoubanId().trim());
                        apabiBookMetaDoubanChecker.setMetaId(apabiBookMetaData.getMetaId().trim());
                        apabiBookMetaDoubanChecker.setDoubanTitle(doubanMeta.getTitle().trim());
                        apabiBookMetaDoubanChecker.setApabiMetaTitle(apabiBookMetaData.getTitle().trim());
                        apabiBookMetaDoubanChecker.setDoubanAuthor(doubanMeta.getAuthor().trim());
                        apabiBookMetaDoubanChecker.setApabiMetaAuthor(apabiBookMetaData.getCreator().trim());
                        apabiBookMetaDoubanChecker.setDoubanPublisher(doubanMeta.getPublisher().trim());
                        apabiBookMetaDoubanChecker.setApabiMetaPublisher(apabiBookMetaData.getPublisher().trim());
                        apabiBookMetaDoubanCheckerDao.insert(apabiBookMetaDoubanChecker);
                    }
                } catch (Exception e) {
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
        List<String> isbn13List = apabiBookMetaDataDao.findIsbn13WithoutDoubanId();
        for (String isbn13 : isbn13List) {
            List<DoubanMeta> doubanMetaList = doubanMetaDao.findByIsbn13(isbn13);
            List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn13(isbn13);
            if (doubanMetaList != null && doubanMetaList.size() == 1 && apabiBookMetaDataList.size() == 1) {
                try {
                    String doubanId = doubanMetaList.get(0).getDoubanId();
                    ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                    apabiBookMetaData.setDoubanId(doubanId);
                    apabiBookMetaDataDao.update(apabiBookMetaData);
                    System.out.println(apabiBookMetaData.getIsbn13());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }

    /**
     * 根据isbn10查询，重新更新apabi_book_metadata中doubanId更新失败的数据
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("checkIsbn10")
    @ResponseBody
    public String checkIsbn10() {
        List<String> isbn10List = apabiBookMetaDataDao.findIsbn10WithoutDoubanId();
        for (String isbn10 : isbn10List) {
            List<DoubanMeta> doubanMetaList = doubanMetaDao.findByIsbn10(isbn10);
            List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn10(isbn10);
            if (doubanMetaList != null && doubanMetaList.size() == 1 && apabiBookMetaDataList.size() == 1) {
                try {
                    String doubanId = doubanMetaList.get(0).getDoubanId();
                    ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                    apabiBookMetaData.setDoubanId(doubanId);
                    apabiBookMetaDataDao.update(apabiBookMetaData);
                    System.out.println(apabiBookMetaData.getIsbn10());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }
}