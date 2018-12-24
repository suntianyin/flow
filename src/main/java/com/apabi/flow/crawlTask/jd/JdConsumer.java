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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = "http:" + jdItemIdQueue.take();
            JdMetadata jdMetadata = CrawlJdUtils.crawlJdMetadataByUrl(url, ip, port);
            jdMetadataDao.insert(jdMetadata);
            LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + jdMetadata.getJdItemId() + "并添加至数据库成功，列表中剩余：" + itemIdQueueCountDownLatch.getCount() + "个数据...");
        } catch (Exception e) {
        } finally {
            itemIdQueueCountDownLatch.countDown();
        }
    }
}