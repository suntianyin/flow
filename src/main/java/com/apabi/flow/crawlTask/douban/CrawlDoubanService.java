package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.DoubanCrawlUrlDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/15 15:01
 **/
//@Order(5)
//@Component
public class CrawlDoubanService implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDoubanService.class);
    @Autowired
    private DoubanCrawlUrlDao doubanCrawlUrlDao;
    @Autowired
    private DoubanMetaDao doubanMetaDao;

    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(startTime);
        LOGGER.info(startDateTime + " spring boot初始化完毕，开始执行douban爬虫....");
        // 定义队列大小
        int queueSize = 100;
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        LinkedBlockingQueue idQueue = new LinkedBlockingQueue(queueSize);
        List<String> idList = new ArrayList<>();
        List<String> urlList = doubanCrawlUrlDao.findAllUrl();
        // 创建代理ip类
        IpPoolUtils ipPoolUtils = new IpPoolUtils();

        // ******************多线程抓取idList开始******************
        CountDownLatch idListCountDownLatch = new CountDownLatch(urlList.size());
        int producerThreadAmount = cpuProcessorAmount * 2;
        ExecutorService idExecutorService = Executors.newFixedThreadPool(producerThreadAmount);
        for (int i = 0; i < urlList.size(); i++) {
            DoubanIdProducer doubanIdProducer = new DoubanIdProducer(urlList.get(i), idList, idListCountDownLatch, ipPoolUtils);
            idExecutorService.execute(doubanIdProducer);
        }
        try {
            idListCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idExecutorService.shutdown();
        // ******************多线程抓取idList结束******************

        // ******************多线程抓取id结束******************
        // id列表中数据计数器
        CountDownLatch idCountDownLatch = new CountDownLatch(idList.size());
        // 创建生产者对象，将id从list中添加到队列中
        DoubanProducer producer = new DoubanProducer(idQueue, "doubanProducer", idList);
        new Thread(producer).start();
        // 设置线程数
        int consumerThreadAmount = 3 * cpuProcessorAmount;
        // 创建消费者对象
        DoubanConsumer consumer = new DoubanConsumer(idQueue, doubanMetaDao, ipPoolUtils, idCountDownLatch);
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(consumerThreadAmount);
        // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
        for (int i = 0; i < idList.size(); i++) {
            // 执行线程中的任务
            executorService.execute(consumer);
        }
        try {
            idCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 关闭线程池
        executorService.shutdown();
        // ******************多线程抓取id结束******************

        long endTime = System.currentTimeMillis();
        String endDateTime = simpleDateFormat.format(endTime);
        LOGGER.info(endDateTime + " douban爬虫执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
    }
}
