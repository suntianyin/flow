package com.apabi.flow.crawlTask.jd;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.jd.model.JdMetadata;
import com.apabi.flow.jd.util.CrawlJdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-5 17:19
 **/
public class JdConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdConsumer.class);
    private LinkedBlockingQueue<String> jdItemIdQueue;
    private CountDownLatch itemIdQueueCountDownLatch;
    private JdMetadataDao jdMetadataDao;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public JdConsumer(LinkedBlockingQueue<String> jdItemIdQueue, CountDownLatch itemIdQueueCountDownLatch, JdMetadataDao jdMetadataDao, NlcIpPoolUtils nlcIpPoolUtils) {
        this.jdItemIdQueue = jdItemIdQueue;
        this.jdMetadataDao = jdMetadataDao;
        this.itemIdQueueCountDownLatch = itemIdQueueCountDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        String ip = "";
        String url = "";
        String port = "";
        String id = "";
        try {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "此时jdItemQueue中有" + jdItemIdQueue.size() + "条数据");
            id = jdItemIdQueue.take();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = "http:" + id;
            JdMetadata result = jdMetadataDao.findById(id);
            if (result == null) {
                JdMetadata jdMetadata = CrawlJdUtils.crawlJdMetadataByUrl(url, ip, port);
                jdMetadataDao.insert(jdMetadata);
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + id + "并添加至数据库成功，列表中剩余：" + itemIdQueueCountDownLatch.getCount() + "个数据...");
            } else {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + id + "在数据库中已存在，列表中剩余：" + itemIdQueueCountDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + id + "失败，列表中剩余：" + itemIdQueueCountDownLatch.getCount() + "个数据...");
        } finally {
            itemIdQueueCountDownLatch.countDown();
        }
    }
}