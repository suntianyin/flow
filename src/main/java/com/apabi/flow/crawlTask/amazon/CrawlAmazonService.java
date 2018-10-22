package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.AmazonCrawlUrlDao;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author pipi
 * @Date 2018/10/15 15:01
 **/
//@Order(2)
//@Component
public class CrawlAmazonService implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(CrawlAmazonService.class);
    @Autowired
    private AmazonCrawlUrlDao amazonCrawlUrlDao;
    @Autowired
    private AmazonMetaDao amazonMetaDao;

    @Scheduled(cron = "0 */240 * * * ?")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("spring boot初始化完毕，开始执行amazon爬虫....");
        int queueSize = 100;
        List<String> idList = new ArrayList<>();
        List<String> urlList = amazonCrawlUrlDao.findAllUrl();
        ArrayBlockingQueue<String> idQueue = new ArrayBlockingQueue<String>(queueSize);
        for (String url : urlList) {
            long startTime = System.currentTimeMillis();
            //TODO 还未分配ip
            // 随机指定代理ip抓取doubanId
            String ip = "";
            String port = "";
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            List<String> amazonIdList = new ArrayList<>();
            try {
                amazonIdList = CrawlAmazonUtils.crawlAmazonIdList(url, ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "，列表大小为：" + amazonIdList.size() + ";耗时为：" + (endTime - startTime) / 1000 + "秒");
            for (String id : amazonIdList) {
                // TODO 还未分配ip
                idList.add(id);
            }
        }
        // 生产者对象
        AmazonProducer producer = new AmazonProducer(idQueue, "amazonProducer", idList);
        new Thread(producer).start();
        // 消费者对象
        AmazonConsumer consumer = new AmazonConsumer(idQueue, amazonMetaDao);
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 3 * cpuProcessorAmount;
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        for (int i = 0; i < idList.size(); i++) {
            executorService.execute(consumer);
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
