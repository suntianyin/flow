package com.apabi.flow.cleanData.controller;

import com.apabi.flow.cleanData.dao.CleanDataDao;
import com.apabi.flow.cleanData.model.CleanData;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.util.Isbn13ToIsbnUtil;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.nlcmarc.dao.NlcCrawlIsbnDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/20 10:07
 **/
@RequestMapping("/cleanData")
@Controller
public class CleanDataController {
    private Logger log = LoggerFactory.getLogger(CleanDataController.class);
    @Autowired
    private CleanDataDao cleanDataDao;
    @Autowired
    private NlcCrawlIsbnDao nlcCrawlIsbnDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;
    private static final int PAGE_SIZE = 10000;

    @RequestMapping("/checkIsbn")
    public void checkIsbn() {
// 获取数据总数
        Integer totalCount = cleanDataDao.getTotalCount();
        // 计算分页处理页数
        Integer pageNumber = (totalCount / PAGE_SIZE) + 1;
        // 将数据的issuedDate结果写入到log日志文件中
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\pirui\\Desktop\\isbn.log", true));
            // 分页处理
            for (int i = 0; i < pageNumber; i++) {
                PageHelper.startPage(i, PAGE_SIZE);
                Page<String> metaIdList = cleanDataDao.findMetaIdsByPage();
                for (String metaIdValue : metaIdList) {
                    List<CleanData> cleanDataList = cleanDataDao.findApabiBookMetaTempPublishByMetaIdIs(metaIdValue);
                    for (CleanData cleanData : cleanDataList) {
                        String isbn = cleanData.getIsbn();
                        log.info(isbn);
                        bufferedWriter.write(isbn);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping("/checkIssuedDate")
    public void checkIssuedDate() {
        // 获取数据总数
        Integer totalCount = cleanDataDao.getTotalCount();
        // 计算分页处理页数
        Integer pageNumber = (totalCount / PAGE_SIZE) + 1;
        // 将数据的issuedDate结果写入到log日志文件中
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\pirui\\Desktop\\issuedDate.log", true));
            // 分页处理
            for (int i = 0; i < pageNumber; i++) {
                PageHelper.startPage(i, PAGE_SIZE);
                Page<String> metaIdList = cleanDataDao.findMetaIdsByPage();
                for (String metaIdValue : metaIdList) {
                    List<CleanData> cleanDataList = cleanDataDao.findApabiBookMetaTempPublishByMetaIdIs(metaIdValue);
                    for (CleanData cleanData : cleanDataList) {
                        String issuedDate = cleanData.getIssuedDate();
                        log.info(issuedDate);
                        bufferedWriter.write(issuedDate == null ? cleanData.getMetaId() : issuedDate);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初次清洗数据
     */
    @RequestMapping("/clean")
    public void clean() {
        // 获取数据总数
        Integer totalCount = cleanDataDao.getTotalCount();
        // 计算分页处理页数
        Integer pageNumber = (totalCount / PAGE_SIZE) + 1;
        // 分页处理
        for (int i = 0; i < pageNumber; i++) {
            Instant start = Instant.now();
            // 更新一页数据完毕后会重新查询数据库，始终处理首页数据。
            PageHelper.startPage(1, PAGE_SIZE);
            Page<String> metaIdList = cleanDataDao.findMetaIdsByPage();
            for (String metaIdValue : metaIdList) {
                List<CleanData> cleanDataList = cleanDataDao.findApabiBookMetaTempPublishByMetaIdIs(metaIdValue);
                for (CleanData cleanData : cleanDataList) {
                    String issuedDate = cleanData.getIssuedDate();
                    String isbn13 = cleanData.getIsbn13();
                    String isbn = cleanData.getIsbn();
                    if (StringUtils.isNotEmpty(isbn13) && StringUtils.isEmpty(isbn)) {
                        // 获取清洗后的isbn的值
                        isbn = Isbn13ToIsbnUtil.transform(isbn13);
                    }
                    if (StringUtils.isNotEmpty(issuedDate)) {
                        issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
                        if (issuedDate.contains(" 00:00:00")) {
                            // 获取清洗后的issuedDate的值
                            issuedDate = issuedDate.replace(" 00:00:00", "");
                        }
                    }
                    try {
                        String metaId = cleanData.getMetaId();
                        if (issuedDate != null) {
                            if (issuedDate.split("-").length != 3) {
                                log.error(metaId);
                            } else {
                                if (issuedDate.split("-")[0].length() != 4) {
                                    log.error(metaId);
                                }
                            }
                        }
                        if (isbn != null && (isbn.contains("(") || isbn.contains(")") || isbn.contains("套") || isbn.contains("（") || isbn.contains("）"))) {
                            log.error(metaId);
                        }
                        cleanDataDao.updateIssuedDateAndIsbn(issuedDate, isbn, metaId);
                        log.info("更新" + cleanData.getMetaId() + "成功!更新后：issuedDate为：" + issuedDate + ";isbn为：" + isbn);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(metaIdValue);
                        log.info("更新" + cleanData.getMetaId() + "失败!原因为：" + e.getMessage());
                    }
                }
            }
            Instant end = Instant.now();
            long time = Duration.between(start, end).toMillis();
            System.out.println("更新第" + i * PAGE_SIZE + "至" + (i + 1) * PAGE_SIZE + "条数据的清洗列表耗时：" + time + "毫秒");
        }
    }


    /**
     * 再次清洗数据
     */
    @RequestMapping("/reClean")
    public void reClean() {
        // 获取数据总数
        Integer totalCount = cleanDataDao.getTotalCountWithoutClean();
        // 计算分页处理页数
        Integer pageNumber = (totalCount / PAGE_SIZE) + 1;
        // 分页处理
        for (int i = 0; i < pageNumber; i++) {
            Instant start = Instant.now();
            PageHelper.startPage(i, PAGE_SIZE);
            Page<String> metaIdList = cleanDataDao.findMetaIdsByPageWithoutClean();
            for (String metaIdValue : metaIdList) {
                List<CleanData> cleanDataList = cleanDataDao.findApabiBookMetaTempPublishByMetaIdIs(metaIdValue);
                for (CleanData cleanData : cleanDataList) {
                    String issuedDate = cleanData.getIssuedDate();
                    String isbn13 = cleanData.getIsbn13();
                    String isbn = cleanData.getIsbn();
                    if (StringUtils.isNotEmpty(isbn13) && StringUtils.isEmpty(isbn)) {
                        // 获取清洗后的isbn的值
                        isbn = Isbn13ToIsbnUtil.transform(isbn13);
                    }
                    if (StringUtils.isNotEmpty(issuedDate)) {
                        issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
                        if (issuedDate.contains(" 00:00:00")) {
                            // 获取清洗后的issuedDate的值
                            issuedDate = issuedDate.replace(" 00:00:00", "");
                        }
                    }
                    try {
                        String metaId = cleanData.getMetaId();
                        if (issuedDate != null) {
                            if (issuedDate.split("-").length != 3) {
                                log.error(metaId);
                            } else {
                                if (issuedDate.split("-")[0].length() != 4) {
                                    log.error(metaId);
                                }
                            }
                        }
                        if (isbn != null && (isbn.contains("(") || isbn.contains(")") || isbn.contains("套") || isbn.contains("（") || isbn.contains("）"))) {
                            log.error(metaId);
                        }
                        cleanDataDao.updateIssuedDateAndIsbn(issuedDate, isbn, metaId);
                        log.info("更新" + cleanData.getMetaId() + "成功!更新后：issuedDate为：" + issuedDate + ";isbn为：" + isbn);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(metaIdValue);
                        log.info("更新" + cleanData.getMetaId() + "失败!原因为：" + e.getMessage());
                    }
                }
            }
            Instant end = Instant.now();
            long time = Duration.between(start, end).toMillis();
            System.out.println("更新第" + i * PAGE_SIZE + "至" + (i + 1) * PAGE_SIZE + "条数据的清洗列表耗时：" + time + "毫秒");
        }
    }


    // 将meta表中的isbn导入到一个中间表
    @ResponseBody
    @RequestMapping("/isbn")
    public String insertIsbn() {
        int pageSize = 10000;
        for (int i = 310; i <= 311; i++) {
            PageHelper.startPage(i, pageSize);
            Page<String> isbnByPageWithoutCrawledNlc = apabiBookMetaDataDao.findIsbnByPageWithoutCrawledNlc();
            for (String s : isbnByPageWithoutCrawledNlc) {
                try {
                    nlcCrawlIsbnDao.insert(s);
                    System.out.println(s);
                } catch (Exception e) {
                }
            }
        }
        System.out.println("导入中间表完成...");
        return "success";
    }

    // 测试apabi_book_metadata_temp的update方法
    @RequestMapping("/apabiTemp")
    public void testApabiBookTempDao() {
        ApabiBookMetaDataTemp apabiBookMetaDataTemp = new ApabiBookMetaDataTemp();
        String metaId = UUIDCreater.nextId();
        apabiBookMetaDataTemp.setMetaId(metaId);
        apabiBookMetaDataTemp.setAbstract_("testAbstract_");
        apabiBookMetaDataTemp.setAlternativeTitle("testAlternativeTitle");
        apabiBookMetaDataTemp.setAmazonId("testAmazonId");
        apabiBookMetaDataTemp.setApabiClass("testApabiClass");
        apabiBookMetaDataTemp.setAuthorIntro("testAuthorIntro");
        apabiBookMetaDataTemp.setBinding("testBinding");
        apabiBookMetaDataTemp.setBookId("testBookId");
        apabiBookMetaDataTemp.setBookPages("testBookPages");
        apabiBookMetaDataTemp.setCalisId("testCalisId");
        apabiBookMetaDataTemp.setCebxObjId("testCebxObjId");
        apabiBookMetaDataTemp.setChapterNum(1);
        apabiBookMetaDataTemp.setContentNum(1);
        apabiBookMetaDataTemp.setCreateTime(new Date());
        apabiBookMetaDataTemp.setIsbn("testIsbn");
        apabiBookMetaDataTemp.setIsbn13("testIsbn13");
        apabiBookMetaDataTemp.setUpdateTime(new Date());
        apabiBookMetaDataTemp.setIssuedDate("testIssuedDate");
        apabiBookMetaDataTempDao.insert(apabiBookMetaDataTemp);

        apabiBookMetaDataTemp.setAbstract_("testAbstract_2");
        apabiBookMetaDataTemp.setAlternativeTitle("testAlternativeTitle2");
        apabiBookMetaDataTemp.setAmazonId("testAmazonId2");
        apabiBookMetaDataTemp.setApabiClass("testApabiClass2");
        apabiBookMetaDataTemp.setAuthorIntro("testAuthorIntro2");
        apabiBookMetaDataTemp.setBinding("testBinding2");
        apabiBookMetaDataTemp.setBookId("testBookId2");
        apabiBookMetaDataTemp.setBookPages("testBookPages2");
        apabiBookMetaDataTemp.setCalisId("testCalisId2");
        apabiBookMetaDataTemp.setCebxObjId("testCebxObjId2");
        apabiBookMetaDataTemp.setChapterNum(2);
        apabiBookMetaDataTemp.setContentNum(2);
        apabiBookMetaDataTemp.setCreateTime(new Date());
        apabiBookMetaDataTemp.setIsbn("testIsbn2");
        apabiBookMetaDataTemp.setIsbn13("testIsbn132");
        apabiBookMetaDataTemp.setUpdateTime(new Date());
        apabiBookMetaDataTemp.setIssuedDate("testIssuedDate2");
        apabiBookMetaDataTempDao.update(apabiBookMetaDataTemp);
        ApabiBookMetaDataTemp byId = apabiBookMetaDataTempDao.findById(metaId);
        List<ApabiBookMetaDataTemp> testIsbn2 = apabiBookMetaDataTempDao.findByIsbn("testIsbn2");
        List<ApabiBookMetaDataTemp> testIsbn132 = apabiBookMetaDataTempDao.findByIsbn13("testIsbn132");
        apabiBookMetaDataTempDao.delete(metaId);
    }
}
