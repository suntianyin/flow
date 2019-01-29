package com.apabi.flow.crawlTask.dangdang;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangCrawlUrlDao;
import com.apabi.flow.dangdang.dao.DangdangMetadataDao;
import com.apabi.flow.dangdang.model.DangdangCrawlUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-10 17:32
 **/
@Component
@Order(2)
public class CrawlDangdangService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDangdangService.class);

    @Autowired
    private DangdangMetadataDao dangdangMetadataDao;

    @Autowired
    private DangdangCrawlUrlDao dangdangCrawlUrlDao;

    /**
     * 定时任务爬虫，务必不可删除
     */
    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(startTime);
        LOGGER.info(startDateTime + " spring boot初始化完毕，开始执行dangdang爬虫....");
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        List<DangdangCrawlUrl> dangdangCrawlUrlList = dangdangCrawlUrlDao.findAll();
        int urlListSize = dangdangCrawlUrlList.size();
        CountDownLatch producerCountDownLatch = new CountDownLatch(urlListSize);
        LinkedBlockingQueue<DangdangCrawlUrl> crawlUrlQueue = new LinkedBlockingQueue(dangdangCrawlUrlList);
        LinkedBlockingQueue<String> dangdangUrlQueue = new LinkedBlockingQueue<>();
        DangdangProducer producer = new DangdangProducer(crawlUrlQueue, dangdangUrlQueue, producerCountDownLatch, nlcIpPoolUtils);
        ExecutorService executorService = Executors.newFixedThreadPool(cpuProcessorAmount * 5);
        for (int i = 0; i < urlListSize; i++) {
            executorService.execute(producer);
        }
        try {
            producerCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int queueSize = dangdangUrlQueue.size();
        CountDownLatch consumerCountDownLatch = new CountDownLatch(queueSize);
        DangdangConsumer consumer = new DangdangConsumer(dangdangUrlQueue, consumerCountDownLatch, dangdangMetadataDao, nlcIpPoolUtils);
        for (int i = 0; i < queueSize; i++) {
            executorService.execute(consumer);
        }
        try {
            consumerCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        long endTime = System.currentTimeMillis();
        String endDateTime = simpleDateFormat.format(endTime);
        LOGGER.info(endDateTime + " amazon爬虫执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
    }
}
