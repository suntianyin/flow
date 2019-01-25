package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-28 9:38
 **/
@Service
@RequestMapping("doubanCrawl")
public class DoubanCrawlService {
    private static final int MIN_ID = 4867554;
    private static final int MAX_ID = 7999999;

    @Autowired
    private DoubanMetaDao doubanMetaDao;

    /**
     * 根据自创doubanId抓取douban
     *
     * @return
     */
    @RequestMapping("crawl")
    public String crawlBySelfCreatedId() {
        int pageSize = 5000;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        int pageNum = ((MAX_ID - MIN_ID) / pageSize) + 1;
        for (int i = 0; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            List<Integer> list = new ArrayList<>();
            for (int j = MIN_ID + i * pageSize; j < MIN_ID + (i + 1) * pageSize; j++) {
                list.add(j);
            }
            LinkedBlockingQueue<Integer> idQueue = new LinkedBlockingQueue<>(list);
            CountDownLatch countDownLatch = new CountDownLatch(pageSize);
            DoubanIdConsumer consumer = new DoubanIdConsumer(idQueue, nlcIpPoolUtils, countDownLatch, doubanMetaDao);
            for (int j = 0; j < pageSize; j++) {
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
