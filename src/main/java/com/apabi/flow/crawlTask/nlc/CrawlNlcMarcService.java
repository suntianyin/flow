package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.dao.NlcCrawlIsbnDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;

import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
    private NlcCrawlIsbnDao nlcCrawlIsbnDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetadataDao;

    @Async
    @Override
    public void run(ApplicationArguments args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // isbn总数
        // int totalCount = nlcCrawlIsbnDao.count();
        int totalCount = nlcCrawlIsbnDao.countSuspect();
        int pageSize = 5000;
        int pageNum = (totalCount / pageSize) + 1;
        // 开始执行任务的时间
        long startTaskTime = System.currentTimeMillis();
        String format = simpleDateFormat.format(startTaskTime);
        logger.info(format + "开始执行nlc爬虫....本次需抓取" + totalCount + "条数据");
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 4 * cpuProcessorAmount;
        // 定义队列大小
        int queueSize = 100;
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        for (int i = 1; i <= pageNum; i++) {
            long startTime = System.currentTimeMillis();
            String startDateTime = simpleDateFormat.format(startTime);
            logger.info(startDateTime + " nlc爬虫抓取第" + (i - 1) * pageSize + "至" + (i) * pageSize + "条...");
            // 当每抓取pageSize条数据，则请求kuaidaili的API，生成最新的ip列表
            NlcIpPoolUtils ipPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            // Page<String> isbnList = nlcCrawlIsbnDao.getIsbnList();
            Page<String> isbnList = nlcCrawlIsbnDao.getSuspectIsbnList();
            // 将isbn添加到队列中
            LinkedBlockingQueue<String> isbnQueue = new LinkedBlockingQueue<>(queueSize);
            // 创建生产者对象
            NlcMarcProducer producer = new NlcMarcProducer(isbnQueue, "nlcMarcProducer", isbnList);
            // 开启生产者线程
            Thread producerThread = new Thread(producer);
            producerThread.start();

            // ******************多线程抓取iso开始******************
            CountDownLatch idCountDownLatch = new CountDownLatch(isbnList.size());
            // 创建消费者对象
            NlcMarcConsumer nlcMarcConsumer = new NlcMarcConsumer(isbnQueue, nlcBookMarcDao, nlcCrawlIsbnDao, apabiBookMetadataDao, ipPoolUtils, idCountDownLatch);
            for (int j = 0; j < isbnList.size(); j++) {
                // 执行线程中的任务
                executorService.execute(nlcMarcConsumer);
            }
            try {
                idCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // ******************多线程抓取iso结束******************

            long endTime = System.currentTimeMillis();
            String endDateTime = simpleDateFormat.format(endTime);
            logger.info(endDateTime + " nlc爬虫抓取第" + (i - 1) * pageSize + "至" + (i) * pageSize + "条执行完毕，共耗时：" + (endTime - startTime) / 1000 + "秒....");
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
