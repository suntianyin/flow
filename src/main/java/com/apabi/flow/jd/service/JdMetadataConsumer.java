package com.apabi.flow.jd.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.dao.JdItemUrlDao;
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
 * @Date 2018-12-5 16:00
 **/
public class JdMetadataConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdMetadataConsumer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinkedBlockingQueue<String> urlQueue;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CountDownLatch countDownLatch;
    private JdMetadataDao jdMetadataDao;
    private JdItemUrlDao jdItemUrlDao;


    public JdMetadataConsumer(LinkedBlockingQueue<String> urlQueue, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch countDownLatch, JdMetadataDao jdMetadataDao, JdItemUrlDao jdItemUrlDao) {
        this.urlQueue = urlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.countDownLatch = countDownLatch;
        this.jdMetadataDao = jdMetadataDao;
        this.jdItemUrlDao = jdItemUrlDao;
    }

    @Override
    public void run() {
        String ip = "";
        String port = "";
        String url = "";
        try {
            url = urlQueue.take();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            JdMetadata jdMetadata = CrawlJdUtils.crawlJdMetadataByUrl(url, ip, port);
            if (jdMetadata != null) {
                try {
                    jdMetadataDao.insert(jdMetadata);
                    LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + jdMetadata.getJdItemId() + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                } catch (Exception e) {
                }
                String urlInDB = url.replace("https:", "");
                jdItemUrlDao.updateHasCrawled(urlInDB);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + url + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
