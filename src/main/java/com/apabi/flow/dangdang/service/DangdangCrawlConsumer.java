package com.apabi.flow.dangdang.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangItemUrlDao;
import com.apabi.flow.dangdang.dao.DangdangMetadataDao;
import com.apabi.flow.dangdang.model.DangdangItemUrl;
import com.apabi.flow.dangdang.model.DangdangMetadata;
import com.apabi.flow.dangdang.util.CrawlDangdangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-10 16:44
 **/
public class DangdangCrawlConsumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DangdangCrawlConsumer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinkedBlockingQueue<DangdangItemUrl> itemUrlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private DangdangItemUrlDao dangdangItemUrlDao;
    private DangdangMetadataDao dangdangMetadataDao;

    public DangdangCrawlConsumer(LinkedBlockingQueue<DangdangItemUrl> itemUrlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, DangdangItemUrlDao dangdangItemUrlDao, DangdangMetadataDao dangdangMetadataDao) {
        this.itemUrlQueue = itemUrlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.dangdangItemUrlDao = dangdangItemUrlDao;
        this.dangdangMetadataDao = dangdangMetadataDao;
    }

    @Override
    public void run() {
        String id = "";
        String url = "";
        String ip = "";
        String port = "";
        String time = "";
        try {
            time = DATE_FORMAT.format(new Date());
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = itemUrlQueue.take().getUrl();
            id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            DangdangMetadata dangdangMetadata = CrawlDangdangUtils.crawlDangdangMetaByUrl(url, ip, port);
            try {
                dangdangMetadataDao.insert(dangdangMetadata);
                dangdangItemUrlDao.updateHasCrawled(url);
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            } catch (Exception e) {
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                dangdangItemUrlDao.updateHasCrawled(url);
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "为组合销售套书，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            dangdangItemUrlDao.updateHasCrawled(url);
        } catch (Exception e) {
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}
