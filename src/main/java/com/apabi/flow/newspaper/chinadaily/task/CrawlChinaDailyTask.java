package com.apabi.flow.newspaper.chinadaily.task;

import com.apabi.flow.newspaper.chinadaily.util.ChinaDailyCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.model.Newspaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/2 11:10
 **/
public class CrawlChinaDailyTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlChinaDailyTask.class);
    private LinkedBlockingQueue<String> urlQueue;
    private NewspaperDao newspaperDao;
    private CountDownLatch countDownLatch;
    private CnrIpPoolUtils cnrIpPoolUtils;

    public CrawlChinaDailyTask(LinkedBlockingQueue<String> urlQueue, NewspaperDao newspaperDao, CountDownLatch countDownLatch, CnrIpPoolUtils cnrIpPoolUtils) {
        this.countDownLatch = countDownLatch;
        this.urlQueue = urlQueue;
        this.cnrIpPoolUtils = cnrIpPoolUtils;
        this.newspaperDao = newspaperDao;
    }

    @Override
    public void run() {
        try {
            String url = urlQueue.take();
            List<Newspaper> newspaperResultList = ChinaDailyCrawlUtils.crawlByUrl(cnrIpPoolUtils, url);
            for (Newspaper newspaper : newspaperResultList) {
                try {
                    newspaperDao.insert(newspaper);
                    // LOGGER.info(Thread.currentThread().getName() + "抓取" + newspaper.getTitle() + "并插入数据库成功");
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
        LOGGER.info("队列中剩余" + countDownLatch.getCount() + "条数据");
    }
}
