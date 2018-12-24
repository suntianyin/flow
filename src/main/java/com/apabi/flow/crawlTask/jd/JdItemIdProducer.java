package com.apabi.flow.crawlTask.jd;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.model.JdItemUrl;
import com.apabi.flow.jd.util.CrawlJdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-5 17:25
 **/
public class JdItemIdProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdItemIdProducer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinkedBlockingQueue<String> urlQueue;
    private LinkedBlockingQueue<String> jdItemIdQueue;
    private CountDownLatch urlListCountDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public JdItemIdProducer(LinkedBlockingQueue urlQueue, LinkedBlockingQueue jdItemIdQueue, CountDownLatch urlListCountDownLatch, NlcIpPoolUtils nlcIpPoolUtils) {
        this.urlListCountDownLatch = urlListCountDownLatch;
        this.jdItemIdQueue = jdItemIdQueue;
        this.urlQueue = urlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        String ip = "";
        String port = "";
        String url = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = urlQueue.take();
            List<JdItemUrl> jdItemUrlList = CrawlJdUtils.crawlItemUrlByPageUrl(url, ip, port);
            for (JdItemUrl jdItemUrl : jdItemUrlList) {
                jdItemIdQueue.put(jdItemUrl.getUrl());
            }
            LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + url + "页面数据成功，列表中剩余：" + urlListCountDownLatch.getCount() + "个数据...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlListCountDownLatch.countDown();
        }
    }
}
