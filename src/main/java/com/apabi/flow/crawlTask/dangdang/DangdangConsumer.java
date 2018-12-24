package com.apabi.flow.crawlTask.dangdang;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangMetadataDao;
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
 * @Date 2018-12-10 17:53
 **/
public class DangdangConsumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DangdangConsumer.class);
    private LinkedBlockingQueue<String> dangdangUrlQueue;
    private CountDownLatch consumerCountDownLatch;
    private DangdangMetadataDao dangdangMetadataDao;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public DangdangConsumer(LinkedBlockingQueue<String> dangdangUrlQueue, CountDownLatch consumerCountDownLatch, DangdangMetadataDao dangdangMetadataDao, NlcIpPoolUtils nlcIpPoolUtils) {
        this.dangdangMetadataDao = dangdangMetadataDao;
        this.consumerCountDownLatch = consumerCountDownLatch;
        this.dangdangUrlQueue = dangdangUrlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }


    @Override
    public void run() {
        String ip = "";
        String port = "";
        String id = "";
        String url = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        try {
            url = dangdangUrlQueue.take();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            DangdangMetadata dangdangMetadata = CrawlDangdangUtils.crawlDangdangMetaByUrl(url, ip, port);
            try {
                dangdangMetadataDao.insert(dangdangMetadata);
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "并添加至数据库成功，列表中剩余：" + consumerCountDownLatch.getCount() + "个数据...");
            } catch (Exception e) {
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "在数据库中已存在，列表中剩余：" + consumerCountDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + id + "失败，列表中剩余：" + consumerCountDownLatch.getCount() + "个数据...");
        } finally {
            consumerCountDownLatch.countDown();
        }
    }
}
