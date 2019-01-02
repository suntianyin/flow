package com.apabi.flow.dangdang.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangCrawlPageUrlDao;
import com.apabi.flow.dangdang.dao.DangdangItemUrlDao;
import com.apabi.flow.dangdang.model.DangdangCrawlPageUrl;
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
 * @Date 2018-12-10 11:13
 **/
public class DangdangCrawlItemConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DangdangCrawlItemConsumer.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinkedBlockingQueue<DangdangCrawlPageUrl> urlQueue;
    private CountDownLatch countDownLatch;
    private DangdangItemUrlDao dangdangItemUrlDao;
    private DangdangCrawlPageUrlDao dangdangCrawlPageUrlDao;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public DangdangCrawlItemConsumer(LinkedBlockingQueue<DangdangCrawlPageUrl> urlQueue, CountDownLatch countDownLatch, DangdangItemUrlDao dangdangItemUrlDao, DangdangCrawlPageUrlDao dangdangCrawlPageUrlDao, NlcIpPoolUtils nlcIpPoolUtils) {
        this.urlQueue = urlQueue;
        this.countDownLatch = countDownLatch;
        this.dangdangItemUrlDao = dangdangItemUrlDao;
        this.dangdangCrawlPageUrlDao = dangdangCrawlPageUrlDao;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        String host = nlcIpPoolUtils.getIp();
        String url = "";
        String ip = "";
        String port = "";
        try {
            url = urlQueue.take().getUrl();
            ip = host.split(":")[0];
            port = host.split(":")[1];

            List<String> dangdangMetaUrlList = CrawlDangdangUtils.crawlDangdangMetaUrlByPage(url, ip, port);
            for (String dangdangMetaUrl : dangdangMetaUrlList) {
                try {
                    DangdangItemUrl dangdangItemUrl = new DangdangItemUrl();
                    dangdangItemUrl.setUrl(dangdangMetaUrl);
                    dangdangItemUrl.setStatus("0");
                    dangdangItemUrlDao.insert(dangdangItemUrl);
                } catch (Exception e) {
                }
            }
            dangdangCrawlPageUrlDao.updateHasCrawled(url);
            LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + url + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } catch (Exception e) {
            LOGGER.info(DATE_FORMAT.format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在dangdang抓取" + url + "并添加至数据库失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
        } finally {
            countDownLatch.countDown();
        }

    }
}
