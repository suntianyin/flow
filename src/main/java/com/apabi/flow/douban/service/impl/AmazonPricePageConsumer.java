package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonCrawlPriceUrlDao;
import com.apabi.flow.douban.model.AmazonCrawlPriceUrl;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-25 9:51
 **/
public class AmazonPricePageConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPricePageConsumer.class);
    private LinkedBlockingQueue<AmazonCrawlPriceUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private AmazonCrawlPriceUrlDao amazonCrawlPriceUrlDao;


    public AmazonPricePageConsumer(LinkedBlockingQueue<AmazonCrawlPriceUrl> urlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, AmazonCrawlPriceUrlDao amazonCrawlPriceUrlDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.amazonCrawlPriceUrlDao = amazonCrawlPriceUrlDao;
    }

    @Override
    public void run() {
        String url = "";
        String ip = "";
        String port = "";
        String pageNum = "";
        AmazonCrawlPriceUrl amazonCrawlPriceUrl = null;
        try {
            amazonCrawlPriceUrl = urlQueue.take();
            url = amazonCrawlPriceUrl.getUrl();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            pageNum = CrawlAmazonUtils.crawlPriceUrlPageNum(url, ip, port);
            amazonCrawlPriceUrl.setPageNum(pageNum);
            amazonCrawlPriceUrlDao.update(amazonCrawlPriceUrl);
            amazonCrawlPriceUrlDao.updateHasCrawled(url);
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "页码为" + pageNum + "；列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
