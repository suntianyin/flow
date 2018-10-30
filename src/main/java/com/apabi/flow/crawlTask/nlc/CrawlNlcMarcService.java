package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Spring boot初始化完毕后开始执行nlcMarc爬虫代码
 *
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
// 先注释掉，不执行爬虫操作
//@Order(1)
//@Component
public class CrawlNlcMarcService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(CrawlNlcMarcService.class);
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetadataDao;

    private IpPoolUtils ipPoolUtils;

    @Scheduled(cron = "0 0/30 * * * ? ")
    public void runTask(){
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        long startTime = System.currentTimeMillis();
        ipPoolUtils = new IpPoolUtils();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(startTime);
        logger.info(startDateTime + " spring boot初始化完毕，开始执行nlc爬虫....");
        PageHelper.startPage(3,10000);
        Page<String> isbnList = apabiBookMetadataDao.findIsbnByPageWithoutCrawledNlc();

        // 定义队列大小
        int queueSize = 100;
        // 将isbn添加到队列中
        ArrayBlockingQueue<String> isbnQueue = new ArrayBlockingQueue<>(queueSize);
        // 创建生产者对象
        NlcMarcProducer producer = new NlcMarcProducer(isbnQueue, "nlcMarcProducer", isbnList);
        // 开启生产者线程
        Thread producerThread = new Thread(producer);
        producerThread.start();
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 4 * cpuProcessorAmount;

        // ******************多线程抓取id开始******************
        CountDownLatch idCountDownLatch = new CountDownLatch(isbnList.size());
        // 创建消费者对象
        NlcMarcConsumer nlcMarcConsumer = new NlcMarcConsumer(isbnQueue, nlcBookMarcDao,apabiBookMetadataDao,ipPoolUtils,idCountDownLatch);
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
        for (int i = 0; i < isbnList.size(); i++) {
            // 执行线程中的任务
            executorService.execute(nlcMarcConsumer);
        }
        try {
            idCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 关闭线程池
        executorService.shutdownNow();
        // ******************多线程抓取id结束******************

        long endTime = System.currentTimeMillis();
        String endDateTime = simpleDateFormat.format(endTime);
        logger.info(endDateTime + " douban爬虫执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
    }
}
