package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.match_data.dao.ApabiBookMetaDoubanMatcherDao;
import com.apabi.flow.match_data.model.ApabiBookMetaDoubanMatcher;
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
 * @Date 2019-1-3 9:07
 **/
public class ApabiBookMetaDoubanMatcherIsbn10Consumer implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaDoubanMatcherIsbn10Consumer.class);
    private LinkedBlockingQueue<DoubanMeta> doubanMetaQueue;
    private CountDownLatch countDownLatch;
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    private ApabiBookMetaDoubanMatcherDao apabiBookMetaDoubanMatcherDao;
    private int pageNum;

    public ApabiBookMetaDoubanMatcherIsbn10Consumer(LinkedBlockingQueue<DoubanMeta> doubanMetaQueue, CountDownLatch countDownLatch, ApabiBookMetaDataDao apabiBookMetaDataDao, ApabiBookMetaDoubanMatcherDao apabiBookMetaDoubanMatcherDao, int pageNum) {
        this.doubanMetaQueue = doubanMetaQueue;
        this.countDownLatch = countDownLatch;
        this.apabiBookMetaDataDao = apabiBookMetaDataDao;
        this.apabiBookMetaDoubanMatcherDao = apabiBookMetaDoubanMatcherDao;
        this.pageNum = pageNum;
    }

    @Override
    public void run() {
        String isbn10 = "";
        DoubanMeta doubanMeta = null;
        try {
            doubanMeta = doubanMetaQueue.take();
            isbn10 = doubanMeta.getIsbn10();
            if (StringUtils.isNotEmpty(isbn10)) {
                List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn10(isbn10);
                if (apabiBookMetaDataList != null) {
                    if (apabiBookMetaDataList.size() == 1) {
                        // 如果douban在meta表中匹配了1项，则直接更新meta表中数据的doubanid字段
                        ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                        apabiBookMetaData.setDoubanId(doubanMeta.getDoubanId());
                        apabiBookMetaDataDao.update(apabiBookMetaData);
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  根据" + isbn10 + "更新meta表数据：" + apabiBookMetaData.getMetaId() + "的doubanId为：" + apabiBookMetaData.getDoubanId());
                    } else if (apabiBookMetaDataList.size() > 1) {
                        // 如果douban在meta表中匹配了多项，则记录到apabi_book_meta_douban_matcher中
                        for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                            ApabiBookMetaDoubanMatcher apabiBookMetaDoubanMatcher = new ApabiBookMetaDoubanMatcher();
                            apabiBookMetaDoubanMatcher.setDoubanAuthor(doubanMeta.getAuthor());
                            apabiBookMetaDoubanMatcher.setApabiAuthor(apabiBookMetaData.getCreator());
                            apabiBookMetaDoubanMatcher.setDoubanPublisher(doubanMeta.getPublisher());
                            apabiBookMetaDoubanMatcher.setApabiPublisher(apabiBookMetaData.getPublisher());
                            apabiBookMetaDoubanMatcher.setDoubanTitle(doubanMeta.getTitle());
                            apabiBookMetaDoubanMatcher.setApabiTitle(apabiBookMetaData.getTitle());
                            apabiBookMetaDoubanMatcher.setIsbn10(isbn10);
                            apabiBookMetaDoubanMatcher.setMetaId(apabiBookMetaData.getMetaId());
                            apabiBookMetaDoubanMatcher.setDoubanId(doubanMeta.getDoubanId());
                            try {
                                apabiBookMetaDoubanMatcherDao.insert(apabiBookMetaDoubanMatcher);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  使用" + Thread.currentThread().getName() + "处理douban映射apabi第" + (pageNum - 1) * 10000 + "条到第" + pageNum * 10000 + "条数据，还剩余" + countDownLatch.getCount() + "条");
            countDownLatch.countDown();
        }
    }
}
