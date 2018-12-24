package com.apabi.flow.isbn_douban_amazon.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDoubanAmazonDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDoubanAmazon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-12 16:21
 **/
public class IsbnDoubanAmazonConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsbnDoubanAmazonConsumer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    private static final String SUCCESS = "1";
    private LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private DoubanMetaDao doubanMetaDao;
    private AmazonMetaDao amazonMetaDao;
    private IsbnDoubanAmazonDao isbnDoubanAmazonDao;

    public IsbnDoubanAmazonConsumer(LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, DoubanMetaDao doubanMetaDao, AmazonMetaDao amazonMetaDao, IsbnDoubanAmazonDao isbnDoubanAmazonDao) {
        this.isbnQueue = isbnQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.doubanMetaDao = doubanMetaDao;
        this.amazonMetaDao = amazonMetaDao;
        this.isbnDoubanAmazonDao = isbnDoubanAmazonDao;
    }

    @Override
    public void run() {
        String ip = "";
        String port = "";
        String isbnValue = "";
        String doubanStatus = "";
        String amazonStatus = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            IsbnDoubanAmazon isbnDoubanAmazon = isbnQueue.take();
            isbnValue = isbnDoubanAmazon.getIsbn();
            // 豆瓣抓取
            doubanStatus = isbnDoubanAmazon.getDoubanStatus();
            if (!SUCCESS.equals(doubanStatus)) {
                DoubanMeta doubanMeta = null;
                try {
                    doubanMeta = CrawlDoubanUtil.crawlDoubanMetaByIsbn(isbnValue, ip, port);
                    if (doubanMeta != null) {
                        try {
                            isbnDoubanAmazonDao.updateDoubanCrawled(isbnValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 标记该isbn已经在douban抓取成功
                        LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                } catch (Exception e) {
                    LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
                if (doubanMeta != null) {
                    try {
                        doubanMetaDao.insert(doubanMeta);
                    } catch (Exception e) {
                        isbnDoubanAmazonDao.updateDoubanCrawled(isbnValue);
                        LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                }
            }
            // 亚马逊抓取
            amazonStatus = isbnDoubanAmazon.getAmazonStatus();
            if (!SUCCESS.equals(amazonStatus)) {
                AmazonMeta amazonMeta = null;
                try {
                    amazonMeta = CrawlAmazonUtils.crawlAmazonMetaByIsbn(isbnValue, ip, port);
                    if (amazonMeta != null) {
                        // 标记该isbn已经在amazon抓取成功
                        isbnDoubanAmazonDao.updateAmazonCrawled(isbnValue);
                        LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                } catch (Exception e) {
                    LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
                if (amazonMeta != null) {
                    try {
                        amazonMetaDao.addAmazonMeta(amazonMeta);
                    } catch (Exception e) {
                        isbnDoubanAmazonDao.updateAmazonCrawled(isbnValue);
                        LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
            isbnDoubanAmazonDao.updateStatusHasCrawled(isbnValue);
        }
    }
}
