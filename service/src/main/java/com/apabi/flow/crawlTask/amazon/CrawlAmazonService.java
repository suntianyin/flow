package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.douban.dao.AmazonCrawlUrlDao;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author pipi
 * @Date 2018/10/15 15:01
 **/
@Order(2)
@Component
public class CrawlAmazonService implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(CrawlAmazonService.class);
    @Autowired
    private AmazonCrawlUrlDao amazonCrawlUrlDao;
    @Autowired
    private AmazonMetaDao amazonMetaDao;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("spring boot初始化完毕，开始执行amazon爬虫....");
        int queueSize = 100;
        List<String> idList = new ArrayList<>();
        List<String> urlList = amazonCrawlUrlDao.findAllUrl();
        ArrayBlockingQueue<String> idQueue = new ArrayBlockingQueue<String>(queueSize);
        for (String url : urlList) {
            //TODO 还未分配ip
            List<String> amazonIdList = CrawlAmazonUtils.crawlAmazonIdList(url, "1", "1");
            for (String id : amazonIdList) {
                // TODO 还未分配ip
                idList.add(id);
            }
        }
        // 生产者对象
        AmazonProducer producer = new AmazonProducer(idQueue,"amazonProducer",idList);
        new Thread(producer).start();
        // 消费者对象
        AmazonConsumer consumer = new AmazonConsumer(idQueue,amazonMetaDao);
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 5 * cpuProcessorAmount;
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        for (int i = 0; i < idList.size(); i++) {
            executorService.execute(consumer);
        }
        // 关闭线程池
        executorService.shutdown();
    }

}
