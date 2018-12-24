package com.apabi.flow.crawlTask.xinhuashudian;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianCrawlUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianMetadataDao;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-19 14:11
 **/
//@Component
//@Order(6)
public class CrawlXinhuashudianService implements ApplicationRunner {

    @Autowired
    private XinhuashudianCrawlUrlDao xinhuashudianCrawlUrlDao;

    @Autowired
    private XinhuashudianMetadataDao xinhuashudianMetadataDao;

    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        List<XinhuashudianCrawlUrl> xinhuashudianCrawlUrlList = xinhuashudianCrawlUrlDao.findAll();
        int queueSize = xinhuashudianCrawlUrlList.size();
        NlcIpPoolUtils nlcIpPoolUtils =new  NlcIpPoolUtils();
        CountDownLatch crawlUrlCountDownLatch = new CountDownLatch(queueSize);
        LinkedBlockingQueue<XinhuashudianCrawlUrl> xinhuashudianCrawlUrlQueue = new LinkedBlockingQueue<>(xinhuashudianCrawlUrlList);
        LinkedBlockingQueue<String> itemUrlQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        XinhuashudianIdProducer producer = new XinhuashudianIdProducer(itemUrlQueue,xinhuashudianCrawlUrlQueue,crawlUrlCountDownLatch,nlcIpPoolUtils);
        for (int i = 0; i < queueSize; i++) {
            executorService.execute(producer);
        }
        try {
            crawlUrlCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int itemQueueSize = itemUrlQueue.size();
        CountDownLatch itemUrlCountDownLatch = new CountDownLatch(itemQueueSize);
        XinhuashudianConsumer consumer = new XinhuashudianConsumer(itemUrlQueue,xinhuashudianMetadataDao,itemUrlCountDownLatch,nlcIpPoolUtils);
        for (int i = 0; i < itemQueueSize; i++) {
            executorService.execute(consumer);
        }
        try {
            itemUrlCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
