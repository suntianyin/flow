package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-28 9:53
 **/
public class DoubanIdConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubanIdConsumer.class);
    private LinkedBlockingQueue<Integer> idQueue;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CountDownLatch countDownLatch;
    private DoubanMetaDao doubanMetaDao;

    public DoubanIdConsumer(LinkedBlockingQueue<Integer> idQueue, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch countDownLatch, DoubanMetaDao doubanMetaDao) {
        this.idQueue = idQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.countDownLatch = countDownLatch;
        this.doubanMetaDao = doubanMetaDao;
    }

    @Override
    public void run() {
        int id = 0;
        String ip = "";
        String port = "";
        try {
            id = idQueue.take();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            DoubanMeta doubanMeta = CrawlDoubanUtil.crawlDoubanMetaById(id + "", ip, port);
            if (doubanMeta != null) {
                try {
                    doubanMetaDao.insert(doubanMeta);
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                } catch (Exception e) {
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            } else {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }

        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}