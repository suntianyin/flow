package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.*;
import com.apabi.flow.douban.model.AmazonCrawlPricePageUrl;
import com.apabi.flow.douban.model.AmazonCrawlPriceUrl;
import com.apabi.flow.douban.model.AmazonItemUrl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-24 16:38
 **/
@RestController
@RequestMapping("amazonCrawl")
public class AmazonCrawlService {
    @Autowired
    private AmazonCrawlUrlDao amazonCrawlUrlDao;

    @Autowired
    private AmazonCrawlPriceUrlDao amazonCrawlPriceUrlDao;

    @Autowired
    private AmazonCrawlPricePageUrlDao amazonCrawlPricePageUrlDao;

    @Autowired
    private AmazonItemUrlDao amazonItemUrlDao;

    @Autowired
    private AmazonMetaDao amazonMetaDao;

    @RequestMapping("crawlPricePageUrl")
    public String crawlPricePageUrl() {
        List<String> urlList = amazonCrawlUrlDao.findAllUrl();
        for (String url : urlList) {
            for (int i = 0; i <= 150; i++) {
                String finalUrl = url + "&low-price=" + i + "&high-price=" + (i + 1);

                AmazonCrawlPriceUrl amazonCrawlPriceUrl = new AmazonCrawlPriceUrl();
                amazonCrawlPriceUrl.setPageNum("0");
                amazonCrawlPriceUrl.setUrl(finalUrl);
                amazonCrawlPriceUrl.setStatus("0");
                amazonCrawlPriceUrlDao.insert(amazonCrawlPriceUrl);
            }
        }
        return "success";
    }

    @RequestMapping("crawlPricePageNum")
    public String crawlPricePageNum() {
        List<AmazonCrawlPriceUrl> amazonCrawlPriceUrlList = amazonCrawlPriceUrlDao.findWithoutCrawled();
        int listSize = amazonCrawlPriceUrlList.size();
        LinkedBlockingQueue<AmazonCrawlPriceUrl> urlQueue = new LinkedBlockingQueue<>(amazonCrawlPriceUrlList);
        CountDownLatch countDownLatch = new CountDownLatch(listSize);
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        AmazonPricePageConsumer consumer = new AmazonPricePageConsumer(urlQueue, countDownLatch, nlcIpPoolUtils, amazonCrawlPriceUrlDao);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < listSize; i++) {
            executorService.execute(consumer);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        return "success";
    }

    @RequestMapping("generatePricePageUrl")
    public String generatePricePageUrl() {
        List<AmazonCrawlPriceUrl> amazonCrawlPriceUrlList = amazonCrawlPriceUrlDao.findAll();
        for (AmazonCrawlPriceUrl amazonCrawlPriceUrl : amazonCrawlPriceUrlList) {
            String pageNum = amazonCrawlPriceUrl.getPageNum();
            int pageNumber = Integer.parseInt(pageNum);
            for (int i = 1; i <= pageNumber; i++) {
                AmazonCrawlPricePageUrl amazonCrawlPricePageUrl = new AmazonCrawlPricePageUrl();
                amazonCrawlPricePageUrl.setUrl(amazonCrawlPriceUrl.getUrl() + "&page=" + i);
                amazonCrawlPricePageUrl.setStatus("0");
                amazonCrawlPricePageUrlDao.insert(amazonCrawlPricePageUrl);
            }
        }
        return "success";
    }

    @RequestMapping("generateAmazonId")
    public String generateAmazonId() {
        int count = amazonCrawlPricePageUrlDao.countWithoutCrawled();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<AmazonCrawlPricePageUrl> amazonCrawlPricePageUrlList = amazonCrawlPricePageUrlDao.findByPageWithoutCrawled();
            int listSize = amazonCrawlPricePageUrlList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            LinkedBlockingQueue<AmazonCrawlPricePageUrl> urlQueue = new LinkedBlockingQueue<>(amazonCrawlPricePageUrlList);
            AmazonCrawlPricePageUrlConsumer consumer = new AmazonCrawlPricePageUrlConsumer(urlQueue, countDownLatch, nlcIpPoolUtils, amazonItemUrlDao, amazonCrawlPricePageUrlDao);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return "success";
    }

    @RequestMapping("crawl")
    public String crawl(){
        int count = amazonItemUrlDao.countWithoutCrawled();
        int pageSize = 5000;
        int pageNum = (count/pageSize)+1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<AmazonItemUrl> amazonItemUrlList = amazonItemUrlDao.findWithoutCrawledByPage();
            int listSize = amazonItemUrlList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            LinkedBlockingQueue<AmazonItemUrl> urlQueue = new LinkedBlockingQueue<>(amazonItemUrlList);
            AmazonItemUrlConsumer consumer = new AmazonItemUrlConsumer(urlQueue,countDownLatch,nlcIpPoolUtils,amazonItemUrlDao,amazonMetaDao);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return "success";
    }
}