package com.apabi.flow.match_data.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDoubanAmazonDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDoubanAmazon;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-4 14:01
 **/
public class CrawlDoubanAmazonNlcInJdNotInDoubanConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDoubanAmazonNlcInJdNotInDoubanConsumer.class);
    private static final String SUCCESS = "1";
    private LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue;
    private CountDownLatch countDownLatch;
    private IsbnDoubanAmazonDao isbnDoubanAmazonDao;
    private DoubanMetaDao doubanMetaDao;
    private AmazonMetaDao amazonMetaDao;
    private NlcBookMarcDao nlcBookMarcDao;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public CrawlDoubanAmazonNlcInJdNotInDoubanConsumer(LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue, CountDownLatch countDownLatch, IsbnDoubanAmazonDao isbnDoubanAmazonDao, DoubanMetaDao doubanMetaDao, AmazonMetaDao amazonMetaDao, NlcBookMarcDao nlcBookMarcDao, NlcIpPoolUtils nlcIpPoolUtils) {
        this.isbnQueue = isbnQueue;
        this.countDownLatch = countDownLatch;
        this.isbnDoubanAmazonDao = isbnDoubanAmazonDao;
        this.doubanMetaDao = doubanMetaDao;
        this.amazonMetaDao = amazonMetaDao;
        this.nlcBookMarcDao = nlcBookMarcDao;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
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
                            // 标记该isbn已经在douban抓取成功
                            isbnDoubanAmazonDao.updateDoubanCrawled(isbnValue);
                            doubanMetaDao.insert(doubanMeta);
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                        } catch (Exception e) {
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbnValue + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
            // 亚马逊抓取
            amazonStatus = isbnDoubanAmazon.getAmazonStatus();
            if (!SUCCESS.equals(amazonStatus)) {
                AmazonMeta amazonMeta = null;
                try {
                    amazonMeta = CrawlAmazonUtils.crawlAmazonMetaByIsbn(isbnValue, ip, port);
                    if (amazonMeta != null) {
                        try {
                            // 标记该isbn已经在amazon抓取成功
                            isbnDoubanAmazonDao.updateAmazonCrawled(isbnValue);
                            amazonMetaDao.insert(amazonMeta);
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                        } catch (Exception e) {
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + isbnValue + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
            // nlc抓取
            NlcBookMarc nlcBookMarc = null;
            List<String> isoList = new ArrayList<>();
            try {
                isoList = CrawlNlcMarcUtil.crawlNlcMarc(isbnValue, ip, port);
                for (String nlcBookMarcValue : isoList) {
                    nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(nlcBookMarcValue);
                    if (nlcBookMarc != null) {
                        try {
                            nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbnValue + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                        } catch (Exception e) {
                            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbnValue + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbnValue + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
            isbnDoubanAmazonDao.updateStatusHasCrawled(isbnValue);
        }
    }
}
