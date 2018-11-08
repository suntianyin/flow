package com.apabi.flow.newspaper.task;

import com.apabi.flow.newspaper.chinanews.util.ChinanewsCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/11/7 14:26
 **/
public class CrawlHtmlContentConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlHtmlContentConsumer.class);
    private ArrayBlockingQueue<String> urlQueue;
    private CountDownLatch countDownLatch;
    private CnrIpPoolUtils cnrIpPoolUtils;
    private NewspaperDao newspaperDao;

    public CrawlHtmlContentConsumer(ArrayBlockingQueue urlQueue, CountDownLatch countDownLatch, CnrIpPoolUtils cnrIpPoolUtils, NewspaperDao newspaperDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.cnrIpPoolUtils = cnrIpPoolUtils;
        this.newspaperDao = newspaperDao;
    }

    @Override
    public void run() {
        try {
            String url = urlQueue.take();
            ChinanewsCrawlUtils.crawlHtmlContentByUrl(url, cnrIpPoolUtils, newspaperDao);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
