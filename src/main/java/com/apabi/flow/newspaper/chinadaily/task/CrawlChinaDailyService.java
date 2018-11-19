package com.apabi.flow.newspaper.chinadaily.task;

import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.crawl_url.model.NewspaperCrawlUrl;
import com.apabi.flow.newspaper.dao.NewspaperCrawlUrlDao;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/14 16:13
 **/
@Controller
@RequestMapping("/chinadaily")
public class CrawlChinaDailyService {
    @Autowired
    private NewspaperCrawlUrlDao newspaperCrawlUrlDao;

    @Autowired
    private NewspaperDao newspaperDao;

    @RequestMapping("/crawl")
    @ResponseBody
    public String crawl() {
        LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue<String>(10000);
        List<NewspaperCrawlUrl> newspaperList = newspaperCrawlUrlDao.findAllBySource("中国日报网");
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < newspaperList.size(); i++) {
            NewspaperCrawlUrl newspaperCrawlUrl = newspaperList.get(i);
            int pageNum = newspaperCrawlUrl.getPageNum();
            for (int j = 2; j < pageNum; j++) {
                String indexPage = newspaperCrawlUrl.getIndexPage();
                String url = String.format(indexPage, j);
                urlQueue.add(url);
            }
        }
        /*CrawlChinaDailyProducer producer = new CrawlChinaDailyProducer(urlList, urlQueue);
        Thread producerThread = new Thread(producer);
        producerThread.start();*/

        CountDownLatch countDownLatch = new CountDownLatch(urlQueue.size());
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        // 消费者
        CrawlChinaDailyTask crawlChinaDailyTask = new CrawlChinaDailyTask(urlQueue, newspaperDao, countDownLatch, cnrIpPoolUtils);
        for (int i = 0; i < urlQueue.size(); i++) {
            executorService.execute(crawlChinaDailyTask);
        }

        return "complete";
    }
}