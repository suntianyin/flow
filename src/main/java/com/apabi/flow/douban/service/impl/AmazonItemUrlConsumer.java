package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonItemUrlDao;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.exception.KindleException;
import com.apabi.flow.douban.model.AmazonItemUrl;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-26 14:12
 **/
public class AmazonItemUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonItemUrlConsumer.class);
    private LinkedBlockingQueue<AmazonItemUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private AmazonItemUrlDao amazonItemUrlDao;
    private AmazonMetaDao amazonMetaDao;

    public AmazonItemUrlConsumer(LinkedBlockingQueue<AmazonItemUrl> urlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, AmazonItemUrlDao amazonItemUrlDao, AmazonMetaDao amazonMetaDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.amazonItemUrlDao = amazonItemUrlDao;
        this.amazonMetaDao = amazonMetaDao;
    }

    @Override
    public void run() {
        AmazonItemUrl amazonItemUrl = null;
        String url = "";
        String ip = "";
        String port = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            amazonItemUrl = urlQueue.take();
            url = amazonItemUrl.getUrl();
            AmazonMeta amazonMeta = CrawlAmazonUtils.crawlAmazonMetaById(url, ip, port);
            if (amazonMeta != null) {
                try {
                    long start = System.currentTimeMillis();
                    amazonMetaDao.insert(amazonMeta);
                    amazonItemUrlDao.updateHasCrawled(url);
                    long end = System.currentTimeMillis();
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "成功，插入耗时" + (end - start) / 1000 + "秒；列表中剩余：" + countDownLatch.getCount() + "个数据...");
                } catch (Exception e) {
                    e.printStackTrace();
                    long start = System.currentTimeMillis();
                    amazonItemUrlDao.updateHasCrawled(url);
                    long end = System.currentTimeMillis();
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "在数据库中已经存在，更新状态耗时" + (end - start) / 1000 + "秒；列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
        } catch (KindleException e) {
            amazonItemUrlDao.updateHasCrawled(url);
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "为Kindle；列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}
