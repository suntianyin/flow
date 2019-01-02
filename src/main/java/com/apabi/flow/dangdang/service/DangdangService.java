package com.apabi.flow.dangdang.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.*;
import com.apabi.flow.dangdang.model.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-7 14:13
 **/
@RestController
@RequestMapping("dangdang")
public class DangdangService {

    @Autowired
    private DangdangCrawlUrlDao dangdangCrawlUrlDao;

    @Autowired
    private DangdangCrawlPageUrlDao dangdangCrawlPageUrlDao;

    @Autowired
    private DangdangItemUrlDao dangdangItemUrlDao;

    @Autowired
    private DangdangMetadataDao dangdangMetadataDao;

    @Autowired
    private DangdangCrawlPriceUrlDao dangdangCrawlPriceUrlDao;

    @Autowired
    private DangdangCrawlPricePageUrlDao dangdangCrawlPricePageUrlDao;

    @RequestMapping("insertUrl")
    public String insertCrawlUrl() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\dangdang.txt"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            String description = line.split("\t")[0];
            String url = line.split("\t")[1];
            String pageNum = line.split("\t")[2];
            DangdangCrawlUrl dangdangCrawlUrl = new DangdangCrawlUrl();
            dangdangCrawlUrl.setDescription(description);
            dangdangCrawlUrl.setUrl(url);
            dangdangCrawlUrl.setPageNum(pageNum);
            dangdangCrawlUrl.setStatus("0");
            System.out.println(dangdangCrawlUrl);
            dangdangCrawlUrlDao.insert(dangdangCrawlUrl);
        }
        return "success";
    }

    @RequestMapping("insertPageUrl")
    public String insertPageCrawlUrl() {
        List<DangdangCrawlUrl> dangdangCrawlUrlList = dangdangCrawlUrlDao.findAll();
        for (DangdangCrawlUrl dangdangCrawlUrl : dangdangCrawlUrlList) {
            String url = dangdangCrawlUrl.getUrl();
            for (int i = 1; i <= 100; i++) {
                String newUrl = url.replace("cp01", "pg" + i + "-cp01");
                DangdangCrawlPageUrl dangdangCrawlPageUrl = new DangdangCrawlPageUrl();
                dangdangCrawlPageUrl.setUrl(newUrl);
                dangdangCrawlPageUrl.setStatus("0");
                dangdangCrawlPageUrlDao.insert(dangdangCrawlPageUrl);
            }
        }
        return "success";
    }


    /**
     * 抓取个网页中的书的url
     *
     * @return
     */
    @RequestMapping("crawlUrl")
    public String crawlDangdangMetaUrl() {
        int pageSize = 5000;
        int count = dangdangCrawlPageUrlDao.countWithoutCrawled();
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<DangdangCrawlPageUrl> dangdangCrawlPageUrlList = dangdangCrawlPageUrlDao.findWithoutCrawledByPage();
            LinkedBlockingQueue<DangdangCrawlPageUrl> urlQueue = new LinkedBlockingQueue(dangdangCrawlPageUrlList);
            int urlListSize = dangdangCrawlPageUrlList.size();
            CountDownLatch countDownLatch = new CountDownLatch(urlListSize);
            DangdangCrawlItemConsumer consumer = new DangdangCrawlItemConsumer(urlQueue, countDownLatch, dangdangItemUrlDao, dangdangCrawlPageUrlDao, nlcIpPoolUtils);
            ExecutorService executorService = Executors.newFixedThreadPool(50);
            for (int j = 0; j < urlListSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }


    @RequestMapping("crawl")
    public String crawlDangdangMeta() {
        int count = dangdangItemUrlDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<DangdangItemUrl> page = dangdangItemUrlDao.findByPageWithoutCrawled();
            int pageCapacity = page.size();
            CountDownLatch countDownLatch = new CountDownLatch(pageCapacity);
            LinkedBlockingQueue<DangdangItemUrl> itemUrlQueue = new LinkedBlockingQueue(page);
            DangdangCrawlConsumer consumer = new DangdangCrawlConsumer(itemUrlQueue, countDownLatch, nlcIpPoolUtils, dangdangItemUrlDao, dangdangMetadataDao);
            for (int j = 0; j < pageCapacity; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }

    @RequestMapping("generatePriceUrl")
    public String generatePriceUrl() {
        List<DangdangCrawlUrl> dangdangCrawlUrlList = dangdangCrawlUrlDao.findAll();
        for (DangdangCrawlUrl dangdangCrawlUrl : dangdangCrawlUrlList) {
            String url = dangdangCrawlUrl.getUrl();
            String preUrl = url.substring(0, url.lastIndexOf("."));
            String postUrl = ".html";
            for (int i = 0; i <= 99; i++) {
                String finalUrl = preUrl + "-lp" + i + "-hp" + (i + 1) + postUrl;
                DangdangCrawlPriceUrl dangdangCrawlPriceUrl = new DangdangCrawlPriceUrl();
                dangdangCrawlPriceUrl.setPageNum("0");
                dangdangCrawlPriceUrl.setUrl(finalUrl);
                dangdangCrawlPriceUrl.setStatus("0");
                dangdangCrawlPriceUrlDao.insert(dangdangCrawlPriceUrl);
            }
            String finalUrl = preUrl + "-lp100" + "-hp" + postUrl;
            DangdangCrawlPriceUrl dangdangCrawlPriceUrl = new DangdangCrawlPriceUrl();
            dangdangCrawlPriceUrl.setPageNum("0");
            dangdangCrawlPriceUrl.setUrl(finalUrl);
            dangdangCrawlPriceUrl.setStatus("0");
            dangdangCrawlPriceUrlDao.insert(dangdangCrawlPriceUrl);
        }
        return "success";
    }

    @RequestMapping("updatePriceUrlPage")
    public String updatePriceUrlPage() {
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        List<DangdangCrawlPriceUrl> dangdangCrawlPriceUrlList = dangdangCrawlPriceUrlDao.findWithoutCrawledByPage();
        int listSize = dangdangCrawlPriceUrlList.size();
        CountDownLatch countDownLatch = new CountDownLatch(listSize);
        LinkedBlockingQueue<DangdangCrawlPriceUrl> dangdangCrawlPriceUrlQueue = new LinkedBlockingQueue(dangdangCrawlPriceUrlList);
        DangdangCrawlPriceUrlConsumer consumer = new DangdangCrawlPriceUrlConsumer(dangdangCrawlPriceUrlQueue, countDownLatch, nlcIpPoolUtils, dangdangCrawlPriceUrlDao);
        ExecutorService executorService = Executors.newFixedThreadPool(50);
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
        List<DangdangCrawlPriceUrl> dangdangCrawlPriceUrlList = dangdangCrawlPriceUrlDao.findAll();
        for (DangdangCrawlPriceUrl dangdangCrawlPriceUrl : dangdangCrawlPriceUrlList) {
            String pageNum = dangdangCrawlPriceUrl.getPageNum();
            int pageNumber = Integer.parseInt(pageNum);
            for (int i = 1; i <= pageNumber; i++) {
                String url = dangdangCrawlPriceUrl.getUrl();
                String prefix = "http://category.dangdang.com/";
                String pageContent = "pg" + i + "-";
                String suffix = url.split("category.dangdang.com/")[1];
                String finalUrl = prefix + pageContent + suffix;
                DangdangCrawlPricePageUrl dangdangCrawlPricePageUrl = new DangdangCrawlPricePageUrl();
                dangdangCrawlPricePageUrl.setUrl(finalUrl);
                dangdangCrawlPricePageUrl.setStatus("0");
                dangdangCrawlPricePageUrlDao.insert(dangdangCrawlPricePageUrl);
            }
        }
        return "success";
    }

    @RequestMapping("crawlItemUrlFromPricePageUrl")
    public String crawlItemUrlFromPricePageUrl() {
        List<DangdangCrawlPricePageUrl> dangdangCrawlPricePageUrlList = dangdangCrawlPricePageUrlDao.findWithoutCrawled();
        int listSize = dangdangCrawlPricePageUrlList.size();
        LinkedBlockingQueue<DangdangCrawlPricePageUrl> urlQueue = new LinkedBlockingQueue(dangdangCrawlPricePageUrlList);
        CountDownLatch countDownLatch = new CountDownLatch(listSize);
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        DangdangCrawlPriceItemConsumer consumer = new DangdangCrawlPriceItemConsumer(urlQueue, countDownLatch, nlcIpPoolUtils, dangdangCrawlPricePageUrlDao, dangdangItemUrlDao);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
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

    @RequestMapping("crawlByPrice")
    public String crawlByPrice() {
        int count = dangdangItemUrlDao.countWithoutCrawled();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<DangdangItemUrl> dangdangItemUrlList = dangdangItemUrlDao.findByPageWithoutCrawled();
            LinkedBlockingQueue<DangdangItemUrl> dangdangItemUrlQueue = new LinkedBlockingQueue<>(dangdangItemUrlList);
            int listSize = dangdangItemUrlList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            DangdangCrawlConsumer consumer = new DangdangCrawlConsumer(dangdangItemUrlQueue, countDownLatch, nlcIpPoolUtils, dangdangItemUrlDao, dangdangMetadataDao);
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

    @RequestMapping("generateAllUrl")
    public String generateAllUrl() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\dd.txt"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            String url = line.split("\t")[0];
            String itemCount = line.split("\t")[1];
            int pageNum = (Integer.parseInt(itemCount) / 60) + 1 > 100 ? 100 : (Integer.parseInt(itemCount) / 60) + 1;
            for (int i = 1; i <= pageNum; i++) {
                DangdangCrawlPageUrl dangdangCrawlPageUrl = new DangdangCrawlPageUrl();
                String prefix = url.substring(0, url.lastIndexOf("/") + 1);
                String suffix = url.substring(url.lastIndexOf("/") + 1, url.length());
                String pageNumber = "pg" + i + "-";
                String finalUrl = prefix + pageNumber + suffix;
                dangdangCrawlPageUrl.setUrl(finalUrl);
                dangdangCrawlPageUrl.setStatus("0");
                try {
                    dangdangCrawlPageUrlDao.insert(dangdangCrawlPageUrl);
                } catch (Exception e) {
                }
            }
        }
        return "success";
    }
}