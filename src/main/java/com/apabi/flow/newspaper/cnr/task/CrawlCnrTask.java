package com.apabi.flow.newspaper.cnr.task;

import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.cnr.util.CnrCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.model.Newspaper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/11/2 11:10
 **/
public class CrawlCnrTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlCnrTask.class);
    private ArrayBlockingQueue<String> urlQueue;
    private NewspaperDao newspaperDao;

    private CountDownLatch countDownLatch;
    private CnrIpPoolUtils cnrIpPoolUtils;
    private CloseableHttpClient closeableHttpClient;

    public CrawlCnrTask(ArrayBlockingQueue<String> urlQueue, NewspaperDao newspaperDao, CountDownLatch countDownLatch, CnrIpPoolUtils cnrIpPoolUtils, CloseableHttpClient closeableHttpClient) {
        this.countDownLatch = countDownLatch;
        this.urlQueue = urlQueue;
        this.cnrIpPoolUtils = cnrIpPoolUtils;
        this.closeableHttpClient = closeableHttpClient;
        this.newspaperDao = newspaperDao;
    }

    @Override
    public void run() {
        try {
            String url = urlQueue.take();
            List<Newspaper> newspaperResultList = CnrCrawlUtils.crawlByUrlEduAndSport(cnrIpPoolUtils, url, closeableHttpClient);
            for (Newspaper newspaper : newspaperResultList) {
                try {
                    newspaperDao.insert(newspaper);
                    String host = cnrIpPoolUtils.getIp();
                    String ip = host.split(":")[0];
                    String port = host.split(":")[1];
                    LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + newspaper.getTitle() + "并插入数据库成功");
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
