package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.AmazonCrawlUrlDao;
import com.apabi.flow.douban.dao.AmazonMetaDao;
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
//@Order(4)
//@Component
public class CrawlAmazonService implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlAmazonService.class);
    @Autowired
    private AmazonCrawlUrlDao amazonCrawlUrlDao;
    @Autowired
    private AmazonMetaDao amazonMetaDao;

    /*@Override
    public void run(ApplicationArguments args) {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(startTime);
        LOGGER.info(startDateTime + " spring boot初始化完毕，开始执行amazon爬虫....");
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        int queueSize = 100;
        List<String> idList = new ArrayList<>();
        List<String> urlList = new ArrayList<>();
        List<String> urlPageList = amazonCrawlUrlDao.findAllUrlWithPage();
        for (int i = 0; i < urlPageList.size(); i++) {
            for (int j = 5; j <= 75; j++) {
                String s = urlPageList.get(i);
                s = s.replace("%d", j + "");
                urlList.add(s);
            }
        }
        LinkedBlockingQueue<String> idQueue = new LinkedBlockingQueue<String>(queueSize);
        // 代理ip工具类
        IpPoolUtils ipPoolUtils = new IpPoolUtils();

        // ******************多线程抓取idList开始******************
        CountDownLatch idListCountDownLatch = new CountDownLatch(urlList.size());
        int producerThreadAmount = cpuProcessorAmount * 5;
        ExecutorService idExecutorService = Executors.newFixedThreadPool(producerThreadAmount);
        for (int i = 0; i < urlList.size(); i++) {
            AmazonIdProducer producer = new AmazonIdProducer(urlList.get(i), idList, idListCountDownLatch, ipPoolUtils);
            idExecutorService.execute(producer);
        }
        try {
            idListCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即关闭线程池
        idExecutorService.shutdown();
        // ******************多线程抓取idList结束******************

        // ******************多线程抓取id开始******************
        // id列表中数据计数器
        CountDownLatch idCountDownLatch = new CountDownLatch(idList.size());
        // 生产者对象
        AmazonProducer producer = new AmazonProducer(idQueue, "amazonProducer", idList);
        new Thread(producer).start();
        // 消费者对象
        AmazonConsumer consumer = new AmazonConsumer(idQueue, amazonMetaDao, ipPoolUtils, idCountDownLatch);

        // 设置线程数
        int consumerThreadAmount = cpuProcessorAmount * 5;
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(consumerThreadAmount);
        for (int i = 0; i < idList.size(); i++) {
            executorService.execute(consumer);
        }
        try {
            idCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即关闭线程池
        executorService.shutdown();
        // ******************多线程抓取id结束******************

        long endTime = System.currentTimeMillis();
        String endDateTime = simpleDateFormat.format(endTime);
        LOGGER.info(endDateTime + " amazon爬虫执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
    }*/

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
        LOGGER.info(startDateTime + " spring boot初始化完毕，开始执行amazon爬虫....");
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        int queueSize = 100;
        List<String> idList = new ArrayList<>();
        List<String> urlList = amazonCrawlUrlDao.findAllUrl();
        LinkedBlockingQueue<String> idQueue = new LinkedBlockingQueue<String>(queueSize);
        // 代理ip工具类
        IpPoolUtils ipPoolUtils = new IpPoolUtils();

        // ******************多线程抓取idList开始******************
        CountDownLatch idListCountDownLatch = new CountDownLatch(urlList.size());
        int producerThreadAmount = cpuProcessorAmount * 3;
        ExecutorService idExecutorService = Executors.newFixedThreadPool(producerThreadAmount);
        for (int i = 0; i < urlList.size(); i++) {
            AmazonIdProducer producer = new AmazonIdProducer(urlList.get(i), idList, idListCountDownLatch, ipPoolUtils);
            idExecutorService.execute(producer);
        }
        try {
            idListCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即关闭线程池
        idExecutorService.shutdown();
        // ******************多线程抓取idList结束******************

        // ******************多线程抓取id开始******************
        // id列表中数据计数器
        CountDownLatch idCountDownLatch = new CountDownLatch(idList.size());
        // 生产者对象
        AmazonProducer producer = new AmazonProducer(idQueue, "amazonProducer", idList);
        new Thread(producer).start();
        // 消费者对象
        AmazonConsumer consumer = new AmazonConsumer(idQueue, amazonMetaDao, ipPoolUtils, idCountDownLatch);

        // 设置线程数
        int consumerThreadAmount = 3 * cpuProcessorAmount;
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(consumerThreadAmount);
        for (int i = 0; i < idList.size(); i++) {
            executorService.execute(consumer);
        }
        try {
            idCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即关闭线程池
        executorService.shutdown();
        // ******************多线程抓取id结束******************

        long endTime = System.currentTimeMillis();
        String endDateTime = simpleDateFormat.format(endTime);
        LOGGER.info(endDateTime + " amazon爬虫执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
    }
}
