package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-9 16:36
 **/
public class ReUpdateDoubanMetaConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReUpdateDoubanMetaConsumer.class);
    private DoubanMetaDao doubanMetaDao;
    private LinkedBlockingQueue<DoubanMeta> doubanMetaQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public ReUpdateDoubanMetaConsumer(DoubanMetaDao doubanMetaDao, LinkedBlockingQueue<DoubanMeta> doubanMetaQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils) {
        this.doubanMetaDao = doubanMetaDao;
        this.doubanMetaQueue = doubanMetaQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        DoubanMeta doubanMeta = null;
        String ip = "";
        String port = "";
        String isbn13 = "";
        try {
            doubanMeta = doubanMetaQueue.take();
            Date createTime = doubanMeta.getCreateTime();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            isbn13 = doubanMeta.getIsbn13();
            if (StringUtils.isNotEmpty(isbn13)) {
                doubanMeta = CrawlDoubanUtil.crawlDoubanMetaByIsbn(isbn13, ip, port);
                try {
                    doubanMeta.setCreateTime(createTime);
                    doubanMeta.setUpdateTime(new Date());
                    doubanMetaDao.update(doubanMeta);
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取并更新" + isbn13 + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                } catch (Exception e) {
                    LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban更新" + isbn13 + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + isbn13 + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}