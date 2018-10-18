package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.douban.dao.DoubanCrawlUrlDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author pipi
 * @Date 2018/10/15 15:01
 **/
//@Order(3)
//@Component
public class CrawlDoubanService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(CrawlDoubanService.class);
    @Autowired
    private DoubanCrawlUrlDao doubanCrawlUrlDao;
    @Autowired
    private DoubanMetaDao doubanMetaDao;

    @Override
    @Scheduled(cron = "0 */1 * * *")
    public void run(ApplicationArguments args) {
        logger.info("spring boot初始化完毕，开始执行douban爬虫....");
        // 定义队列大小
        int queueSize = 100;
        ArrayBlockingQueue idQueue = new ArrayBlockingQueue(queueSize);
        List<String> idList = new ArrayList<>();
        List<String> urlList = doubanCrawlUrlDao.findAllUrl();

        int j = 0;
        for (String url : urlList) {
            // TODO 指定分配ip的算法进行抓取
            // TODO 还没有分配ip
            // 随机指定代理ip抓取doubanId
            if (j >= 6 && j < 10) {
                List<String> doubanIdList = CrawlDoubanUtil.crawlDoubanIdList(url, "1", "1");
                for (String id : doubanIdList) {
                    idList.add(id);
                }
            }
            j++;
            if (j > 10) {
                break;
            }
        }
        // 创建生产者对象，将id从list中添加到队列中
        DoubanProducer producer = new DoubanProducer(idQueue, "doubanProducer", idList);
        new Thread(producer).start();
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 5 * cpuProcessorAmount;
        // 创建消费者对象
        DoubanConsumer consumer = new DoubanConsumer(idQueue, doubanMetaDao);
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
        for (int i = 0; i < idList.size(); i++) {
            // 执行线程中的任务
            executorService.execute(consumer);
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
