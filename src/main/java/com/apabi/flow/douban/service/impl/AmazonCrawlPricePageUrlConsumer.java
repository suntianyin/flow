package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonCrawlPricePageUrlDao;
import com.apabi.flow.douban.dao.AmazonItemUrlDao;
import com.apabi.flow.douban.model.AmazonCrawlPricePageUrl;
import com.apabi.flow.douban.model.AmazonItemUrl;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-25 16:02
 **/
public class AmazonCrawlPricePageUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonCrawlPricePageUrlConsumer.class);
    private LinkedBlockingQueue<AmazonCrawlPricePageUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private AmazonItemUrlDao amazonItemUrlDao;
    private AmazonCrawlPricePageUrlDao amazonCrawlPricePageUrlDao;


    public AmazonCrawlPricePageUrlConsumer(LinkedBlockingQueue<AmazonCrawlPricePageUrl> urlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, AmazonItemUrlDao amazonItemUrlDao,AmazonCrawlPricePageUrlDao amazonCrawlPricePageUrlDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.amazonCrawlPricePageUrlDao = amazonCrawlPricePageUrlDao;
        this.amazonItemUrlDao = amazonItemUrlDao;
    }

    @Override
    public void run() {
        AmazonCrawlPricePageUrl amazonCrawlPricePageUrl = null;
        String url = "";
        String ip = "";
        String port = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            amazonCrawlPricePageUrl = urlQueue.take();
            url = amazonCrawlPricePageUrl.getUrl();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            List<String> list = CrawlAmazonUtils.crawlAmazonIdList(url, ip, port);
            if (list != null && list.size() > 0) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                amazonCrawlPricePageUrlDao.updateHasCrawled(url);
                for (String id : list) {
                    AmazonItemUrl amazonItemUrl = new AmazonItemUrl();
                    amazonItemUrl.setUrl(id);
                    amazonItemUrl.setStatus("0");
                    try {
                        amazonItemUrlDao.insert(amazonItemUrl);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + url + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}
