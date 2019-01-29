package com.apabi.flow.crawlTask.jd;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.dao.JdCrawlUrlDao;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.jd.model.JdCrawlUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-5 17:19
 **/
@Order(3)
@Component
public class CrawlJdService implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlJdService.class);

    @Autowired
    private JdCrawlUrlDao jdCrawlUrlDao;

    @Autowired
    private JdMetadataDao jdMetadataDao;

    /**
     * 定时任务爬虫，务必不可删除
     */
    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        List<JdCrawlUrl> jdCrawlUrlList = jdCrawlUrlDao.findAll();
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        int listSize = jdCrawlUrlList.size();
        LOGGER.info("京东定时任务从数据库中查询到URL列表大小为：" + listSize);
        LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue();
        LinkedBlockingQueue<String> jdItemIdQueue = new LinkedBlockingQueue();
        for (JdCrawlUrl jdCrawlUrl : jdCrawlUrlList) {
            try {
                String url = jdCrawlUrl.getUrl();
                urlQueue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CountDownLatch urlListCountDownLatch = new CountDownLatch(listSize);
        JdItemIdProducer producer = new JdItemIdProducer(urlQueue, jdItemIdQueue, urlListCountDownLatch, nlcIpPoolUtils);
        ExecutorService executorService = Executors.newFixedThreadPool(cpuProcessorAmount * 3);
        for (int i = 0; i < listSize; i++) {
            executorService.execute(producer);
        }
        try {
            urlListCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int itemIdQueueSize = jdItemIdQueue.size();
        LOGGER.info("京东定时任务由生产者生产的itemUrl大小为：" + itemIdQueueSize);
        CountDownLatch itemIdQueueCountDownLatch = new CountDownLatch(itemIdQueueSize);
        JdConsumer consumer = new JdConsumer(jdItemIdQueue, itemIdQueueCountDownLatch, jdMetadataDao, nlcIpPoolUtils);
        for (int i = 0; i < itemIdQueueSize; i++) {
            executorService.execute(consumer);
        }
        try {
            itemIdQueueCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}