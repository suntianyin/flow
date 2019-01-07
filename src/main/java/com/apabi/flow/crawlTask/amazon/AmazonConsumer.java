package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
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
 * @Date 2018/10/17 15:03
 **/
public class AmazonConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonConsumer.class);
    private LinkedBlockingQueue<String> idQueue;
    private AmazonMetaDao amazonMetaDao;
    private volatile IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public AmazonConsumer(LinkedBlockingQueue<String> idQueue, AmazonMetaDao amazonMetaDao, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.idQueue = idQueue;
        this.amazonMetaDao = amazonMetaDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        AmazonMeta amazonMeta = null;
        try {
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            id = idQueue.take();
            amazonMeta = CrawlAmazonUtils.crawlAmazonMetaById(id, ip, port);
            try {
                amazonMetaDao.insert(amazonMeta);
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + id + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + id + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}