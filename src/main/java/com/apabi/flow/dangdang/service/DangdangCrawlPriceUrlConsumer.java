package com.apabi.flow.dangdang.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangCrawlPriceUrlDao;
import com.apabi.flow.dangdang.model.DangdangCrawlPriceUrl;
import com.apabi.flow.dangdang.util.CrawlDangdangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-20 16:29
 **/
public class DangdangCrawlPriceUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DangdangCrawlPriceUrlConsumer.class);
    private LinkedBlockingQueue<DangdangCrawlPriceUrl> dangdangCrawlPriceUrlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private DangdangCrawlPriceUrlDao dangdangCrawlPriceUrlDao;

    public DangdangCrawlPriceUrlConsumer(LinkedBlockingQueue<DangdangCrawlPriceUrl> dangdangCrawlPriceUrlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, DangdangCrawlPriceUrlDao dangdangCrawlPriceUrlDao) {
        this.dangdangCrawlPriceUrlQueue = dangdangCrawlPriceUrlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.dangdangCrawlPriceUrlDao = dangdangCrawlPriceUrlDao;
    }

    @Override
    public void run() {
        DangdangCrawlPriceUrl dangdangCrawlPriceUrl = null;
        String ip = "";
        String port = "";
        String url = "";
        try {
            dangdangCrawlPriceUrl = dangdangCrawlPriceUrlQueue.take();
            url = dangdangCrawlPriceUrl.getUrl();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            String pageNum = CrawlDangdangUtils.crawlDangdangPriceUrlPage(url, ip, port);
            if (!"0".equalsIgnoreCase(pageNum + "")) {
                dangdangCrawlPriceUrl.setPageNum(pageNum + "");
                dangdangCrawlPriceUrl.setStatus("1");
                dangdangCrawlPriceUrlDao.update(dangdangCrawlPriceUrl);
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + url + "抓取的页数为：" + pageNum + "，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
