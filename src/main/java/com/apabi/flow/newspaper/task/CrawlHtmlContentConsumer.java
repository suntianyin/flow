package com.apabi.flow.newspaper.task;

import com.apabi.flow.newspaper.chinadaily.util.ChinaDailyCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 多线程抓取报纸消费者
 *
 * @Author pipi
 * @Date 2018/11/7 14:26
 **/
public class CrawlHtmlContentConsumer implements Runnable {
    private LinkedBlockingQueue<String> urlQueue;
    private CountDownLatch countDownLatch;
    private CnrIpPoolUtils cnrIpPoolUtils;
    private NewspaperDao newspaperDao;

    public CrawlHtmlContentConsumer(LinkedBlockingQueue urlQueue, CountDownLatch countDownLatch, CnrIpPoolUtils cnrIpPoolUtils, NewspaperDao newspaperDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.cnrIpPoolUtils = cnrIpPoolUtils;
        this.newspaperDao = newspaperDao;
    }

    /**
     * 从阻塞队列中不断取出url，对报纸页面html信息进行抓取
     */
    @Override
    public void run() {
        try {
            String url = urlQueue.take();
            ChinaDailyCrawlUtils.crawlHtmlContentByUrl(url, cnrIpPoolUtils, newspaperDao);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
