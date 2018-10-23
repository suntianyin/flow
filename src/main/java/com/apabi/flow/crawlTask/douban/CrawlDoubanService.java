package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.DoubanCrawlUrlDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
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
//@Order(3)
//@Component
public class CrawlDoubanService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(CrawlDoubanService.class);
    @Autowired
    private DoubanCrawlUrlDao doubanCrawlUrlDao;
    @Autowired
    private DoubanMetaDao doubanMetaDao;

    @Scheduled(cron = "0 */240 * * * ?")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("spring boot初始化完毕，开始执行douban爬虫....");
        // 定义队列大小
        int queueSize = 100;
        ArrayBlockingQueue idQueue = new ArrayBlockingQueue(queueSize);
        List<String> idList = new ArrayList<>();
        List<String> urlList = doubanCrawlUrlDao.findAllUrl();

        // ==================多线程抓取idList开始==================
        //CountDownLatch countDownLatch = new CountDownLatch(urlList.size());
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < urlList.size(); i++) {

            service.execute(null);

            long startTime = System.currentTimeMillis();
            // 随机指定代理ip抓取doubanId
            String ip = "";
            String port = "";
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            List<String> doubanIdList = new ArrayList<>();
            try {
                doubanIdList = CrawlDoubanUtil.crawlDoubanIdList(urlList.get(i), ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取第" + (i + 1) + "个url列表：" + urlList.get(i) + "，列表大小为：" + doubanIdList.size() + ";耗时为：" + (endTime - startTime) / 1000 + "秒");
            for (String id : doubanIdList) {
                idList.add(id);
            }
        }
        // ==================多线程抓取idList结束==================

        /*// ==================多线程抓取idList开始==================
        //CountDownLatch countDownLatch = new CountDownLatch(urlList.size());
        for (int i = 0; i < urlList.size(); i++) {
            long startTime = System.currentTimeMillis();
            // 随机指定代理ip抓取doubanId
            String ip = "";
            String port = "";
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            List<String> doubanIdList = new ArrayList<>();
            try {
                doubanIdList = CrawlDoubanUtil.crawlDoubanIdList(urlList.get(i), ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取第" + (i + 1) + "个url列表：" + urlList.get(i) + "，列表大小为：" + doubanIdList.size() + ";耗时为：" + (endTime - startTime) / 1000 + "秒");
            for (String id : doubanIdList) {
                idList.add(id);
            }
        }
        // ==================多线程抓取idList结束==================*/

        // 创建生产者对象，将id从list中添加到队列中
        DoubanProducer producer = new DoubanProducer(idQueue, "doubanProducer", idList);
        new Thread(producer).start();
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 3 * cpuProcessorAmount;
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
