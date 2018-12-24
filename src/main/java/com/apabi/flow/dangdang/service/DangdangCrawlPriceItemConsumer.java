package com.apabi.flow.dangdang.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangCrawlPricePageUrlDao;
import com.apabi.flow.dangdang.dao.DangdangItemUrlDao;
import com.apabi.flow.dangdang.model.DangdangCrawlPricePageUrl;
import com.apabi.flow.dangdang.model.DangdangItemUrl;
import com.apabi.flow.dangdang.util.CrawlDangdangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-21 14:26
 **/
public class DangdangCrawlPriceItemConsumer implements Runnable {
    private static final Logger LOGGER  = LoggerFactory.getLogger(DangdangCrawlPriceItemConsumer.class);
    private LinkedBlockingQueue<DangdangCrawlPricePageUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private DangdangCrawlPricePageUrlDao dangdangCrawlPricePageUrlDao;
    private DangdangItemUrlDao dangdangItemUrlDao;

    public DangdangCrawlPriceItemConsumer(LinkedBlockingQueue<DangdangCrawlPricePageUrl> urlQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, DangdangCrawlPricePageUrlDao dangdangCrawlPricePageUrlDao, DangdangItemUrlDao dangdangItemUrlDao) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.dangdangCrawlPricePageUrlDao = dangdangCrawlPricePageUrlDao;
        this.dangdangItemUrlDao = dangdangItemUrlDao;
    }

    @Override
    public void run() {
        DangdangCrawlPricePageUrl dangdangCrawlPricePageUrl = null;
        String url = "";
        String ip = "";
        String port = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            dangdangCrawlPricePageUrl = urlQueue.take();
            url = dangdangCrawlPricePageUrl.getUrl();
            List<String> dangdangMetaUrlList = CrawlDangdangUtils.crawlDangdangMetaUrlByPage(url, ip, port);
            dangdangCrawlPricePageUrlDao.updateHasCrawled(url);
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + url + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            for (String dangdangMetaUrl : dangdangMetaUrlList) {
                DangdangItemUrl dangdangItemUrl = new DangdangItemUrl();
                dangdangItemUrl.setUrl(dangdangMetaUrl);
                dangdangItemUrl.setStatus("0");
                try {
                    dangdangItemUrlDao.insert(dangdangItemUrl);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
