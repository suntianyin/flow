package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
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
 * @Date 2018/10/16 16:19
 **/
public class DoubanConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubanConsumer.class);
    private LinkedBlockingQueue<String> idQueue;
    private DoubanMetaDao doubanMetaDao;
    private IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public DoubanConsumer(LinkedBlockingQueue<String> idQueue, DoubanMetaDao doubanMetaDao, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.idQueue = idQueue;
        this.doubanMetaDao = doubanMetaDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        DoubanMeta doubanMeta = null;
        try {
            id = idQueue.take();
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            DoubanMeta result = doubanMetaDao.findById(id);
            if (result == null) {
                doubanMeta = CrawlDoubanUtil.crawlDoubanMetaById(id, ip, port);
                doubanMetaDao.insert(doubanMeta);
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            } else {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}