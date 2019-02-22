package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.match_data.dao.ApabiBookMetaNlcMatcherDao;
import com.apabi.flow.match_data.model.ApabiBookMetaNlcMatcher;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-7 17:52
 **/
public class ApabiBookMetaNlcMatcherIsbnConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaNlcMatcherIsbnConsumer.class);
    private LinkedBlockingQueue<NlcBookMarc> marcQueue;
    private CountDownLatch countDownLatch;
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    private ApabiBookMetaNlcMatcherDao apabiBookMetaNlcMatcherDao;

    public ApabiBookMetaNlcMatcherIsbnConsumer(LinkedBlockingQueue<NlcBookMarc> marcQueue, CountDownLatch countDownLatch, ApabiBookMetaDataDao apabiBookMetaDataDao, ApabiBookMetaNlcMatcherDao apabiBookMetaNlcMatcherDao) {
        this.marcQueue = marcQueue;
        this.countDownLatch = countDownLatch;
        this.apabiBookMetaDataDao = apabiBookMetaDataDao;
        this.apabiBookMetaNlcMatcherDao = apabiBookMetaNlcMatcherDao;
    }

    @Override
    public void run() {
        String isbn = "";
        NlcBookMarc nlcBookMarc = null;
        try {
            nlcBookMarc = marcQueue.take();
            isbn = nlcBookMarc.getIsbn();
            if (StringUtils.isNotEmpty(isbn)) {
                List<ApabiBookMetaData> apabiBookMetaDataListByIsbn = apabiBookMetaDataDao.findByIsbn(isbn);
                // 根据ISBN查询
                if (apabiBookMetaDataListByIsbn != null && apabiBookMetaDataListByIsbn.size() > 0) {
                    if (apabiBookMetaDataListByIsbn.size() == 1 && StringUtils.isEmpty(apabiBookMetaDataListByIsbn.get(0).getNlibraryId())) {
                        // 匹配了一个
                        ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataListByIsbn.get(0);
                        if (StringUtils.isEmpty(apabiBookMetaData.getNlibraryId())) {
                            apabiBookMetaData.setNlibraryId(nlcBookMarc.getNlcMarcId());
                            try {
                                apabiBookMetaDataDao.update(apabiBookMetaData);
                                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  根据" + isbn + "更新meta表数据：" + apabiBookMetaData.getMetaId() + "的nlibraryId为：" + apabiBookMetaData.getNlibraryId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (apabiBookMetaDataListByIsbn.size() > 1){
                        // 匹配了多个
                        for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataListByIsbn) {
                            ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher = new ApabiBookMetaNlcMatcher();
                            apabiBookMetaNlcMatcher.setIsbn(isbn);
                            apabiBookMetaNlcMatcher.setMetaId(apabiBookMetaData.getMetaId());
                            apabiBookMetaNlcMatcher.setApabiAuthor(apabiBookMetaData.getCreator());
                            apabiBookMetaNlcMatcher.setApabiPublisher(apabiBookMetaData.getPublisher());
                            apabiBookMetaNlcMatcher.setApabiTitle(apabiBookMetaData.getTitle());
                            apabiBookMetaNlcMatcher.setNlcMarcId(nlcBookMarc.getNlcMarcId());
                            apabiBookMetaNlcMatcher.setNlcMarcAuthor(nlcBookMarc.getAuthor());
                            apabiBookMetaNlcMatcher.setNlcMarcPublisher(nlcBookMarc.getPublisher());
                            apabiBookMetaNlcMatcher.setNlcMarcTitle(nlcBookMarc.getTitle());
                            apabiBookMetaNlcMatcherDao.insert(apabiBookMetaNlcMatcher);
                        }
                    }
                } else {
                    // 如果根据ISBN查询不到，则按照ISBN10或者ISBN13查询
                    isbn = isbn.replaceAll("-", "");
                    if (isbn.length() == 10) {
                        List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn10(isbn);
                        if (apabiBookMetaDataList != null) {
                            if (apabiBookMetaDataList.size() == 1 && StringUtils.isEmpty(apabiBookMetaDataList.get(0).getNlibraryId())) {
                                // 匹配了一个
                                ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                                if (StringUtils.isEmpty(apabiBookMetaData.getNlibraryId())) {
                                    apabiBookMetaData.setNlibraryId(nlcBookMarc.getNlcMarcId());
                                    try {
                                        apabiBookMetaDataDao.update(apabiBookMetaData);
                                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  根据" + isbn + "更新meta表数据：" + apabiBookMetaData.getMetaId() + "的nlibraryId为：" + apabiBookMetaData.getNlibraryId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (apabiBookMetaDataList.size() > 1) {
                                // 匹配了多个
                                for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                                    ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher = new ApabiBookMetaNlcMatcher();
                                    apabiBookMetaNlcMatcher.setIsbn10(isbn);
                                    apabiBookMetaNlcMatcher.setMetaId(apabiBookMetaData.getMetaId());
                                    apabiBookMetaNlcMatcher.setApabiAuthor(apabiBookMetaData.getCreator());
                                    apabiBookMetaNlcMatcher.setApabiPublisher(apabiBookMetaData.getPublisher());
                                    apabiBookMetaNlcMatcher.setApabiTitle(apabiBookMetaData.getTitle());
                                    apabiBookMetaNlcMatcher.setNlcMarcId(nlcBookMarc.getNlcMarcId());
                                    apabiBookMetaNlcMatcher.setNlcMarcAuthor(nlcBookMarc.getAuthor());
                                    apabiBookMetaNlcMatcher.setNlcMarcPublisher(nlcBookMarc.getPublisher());
                                    apabiBookMetaNlcMatcher.setNlcMarcTitle(nlcBookMarc.getTitle());
                                    apabiBookMetaNlcMatcherDao.insert(apabiBookMetaNlcMatcher);
                                }
                            }
                        }
                    } else if (isbn.length() == 13) {
                        List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn13(isbn);
                        if (apabiBookMetaDataList != null) {
                            if (apabiBookMetaDataList.size() == 1 && StringUtils.isEmpty(apabiBookMetaDataList.get(0).getNlibraryId())) {
                                // 匹配了一个
                                ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                                if (StringUtils.isEmpty(apabiBookMetaData.getNlibraryId())) {
                                    apabiBookMetaData.setNlibraryId(nlcBookMarc.getNlcMarcId());
                                    try {
                                        apabiBookMetaDataDao.update(apabiBookMetaData);
                                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  根据" + isbn + "更新meta表数据：" + apabiBookMetaData.getMetaId() + "的nlibraryId为：" + apabiBookMetaData.getNlibraryId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (apabiBookMetaDataList.size() > 1) {
                                // 匹配了多个
                                for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                                    ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher = new ApabiBookMetaNlcMatcher();
                                    apabiBookMetaNlcMatcher.setIsbn13(isbn);
                                    apabiBookMetaNlcMatcher.setMetaId(apabiBookMetaData.getMetaId());
                                    apabiBookMetaNlcMatcher.setApabiAuthor(apabiBookMetaData.getCreator());
                                    apabiBookMetaNlcMatcher.setApabiPublisher(apabiBookMetaData.getPublisher());
                                    apabiBookMetaNlcMatcher.setApabiTitle(apabiBookMetaData.getTitle());
                                    apabiBookMetaNlcMatcher.setNlcMarcId(nlcBookMarc.getNlcMarcId());
                                    apabiBookMetaNlcMatcher.setNlcMarcAuthor(nlcBookMarc.getAuthor());
                                    apabiBookMetaNlcMatcher.setNlcMarcPublisher(nlcBookMarc.getPublisher());
                                    apabiBookMetaNlcMatcher.setNlcMarcTitle(nlcBookMarc.getTitle());
                                    apabiBookMetaNlcMatcherDao.insert(apabiBookMetaNlcMatcher);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        } finally {
//            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  使用" + Thread.currentThread().getName() + "处理nlc的" + nlcBookMarc.getNlcMarcId() + "，映射apabi第" + (pageNum - 1) * 10000 + "条到第" + pageNum * 10000 + "条数据，还剩余" + countDownLatch.getCount() + "条");
            countDownLatch.countDown();
        }
    }
}
