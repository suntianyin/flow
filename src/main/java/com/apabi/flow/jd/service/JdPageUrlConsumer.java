package com.apabi.flow.jd.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.dao.JdCrawlPageUrlDao;
import com.apabi.flow.jd.dao.JdItemUrlDao;
import com.apabi.flow.jd.model.JdCrawlPageUrl;
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
 * @Date 2018-12-5 9:36
 **/
public class JdPageUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdPageUrlConsumer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinkedBlockingQueue<JdCrawlPageUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private JdCrawlPageUrlDao jdCrawlPageUrlDao;
    private JdItemUrlDao jdItemUrlDao;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public JdPageUrlConsumer(LinkedBlockingQueue<JdCrawlPageUrl> urlQueue, CountDownLatch countDownLatch, JdCrawlPageUrlDao jdCrawlPageUrlDao, JdItemUrlDao jdItemUrlDao, NlcIpPoolUtils nlcIpPoolUtils) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.jdCrawlPageUrlDao = jdCrawlPageUrlDao;
        this.jdItemUrlDao = jdItemUrlDao;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        String url = "";
        String ip = "";
        String port = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = urlQueue.take().getUrl();
            List<JdItemUrl> itemList = CrawlJdUtils.crawlItemUrlByPageUrl(url, ip, port);
            for (JdItemUrl jdItemUrl : itemList) {
                try {
                    jdItemUrlDao.insert(jdItemUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 将该url标记为已抓取
            jdCrawlPageUrlDao.updateHasCrawled(url);
            Date date = new Date();
            String time = DATE_FORMAT.format(date);
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在jd抓取" + url + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
