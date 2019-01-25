package com.apabi.flow.isbn_douban_amazon.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDangdangDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDangdang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-18 15:36
 **/
public class IsbnDangdangCrawlAmazonConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsbnDangdangCrawlAmazonConsumer.class);
    private LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private AmazonMetaDao amazonMetaDao;
    private IsbnDangdangDao isbnDangdangDao;

    public IsbnDangdangCrawlAmazonConsumer(LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils,AmazonMetaDao amazonMetaDao, IsbnDangdangDao isbnDangdangDao) {
        this.isbnDangdangQueue = isbnDangdangQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.amazonMetaDao = amazonMetaDao;
        this.isbnDangdangDao = isbnDangdangDao;
    }

    @Override
    public void run() {
        IsbnDangdang isbnDangdang = null;
        String ip = "";
        String port = "";
        String isbn = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            isbnDangdang = isbnDangdangQueue.take();
            isbn = isbnDangdang.getIsbn();
            try {
                AmazonMeta amazonMeta = CrawlAmazonUtils.crawlAmazonMetaByIsbn(isbn, ip, port);
                if (amazonMeta != null) {
                    isbnDangdangDao.updateAmazonHasCrawled(isbn);
                    try {
                        amazonMetaDao.insert(amazonMeta);
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在amazon抓取" + isbn + "并插入数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    } catch (Exception e) {
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在amazon抓取" + isbn + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                }
            } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在amazon抓取" + isbn + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
