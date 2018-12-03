package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/17 15:03
 **/
public class AmazonConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AmazonConsumer.class);
    private LinkedBlockingQueue<String> idQueue;
    private AmazonMetaDao amazonMetaDao;
    private volatile IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public AmazonConsumer(LinkedBlockingQueue<String> idQueue, AmazonMetaDao amazonMetaDao, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.idQueue = idQueue;
        this.amazonMetaDao = amazonMetaDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String id = "";
        AmazonMeta amazonMeta = null;
        try {
            id = idQueue.take();
            amazonMeta = CrawlAmazonUtils.crawlAmazonMetaById(id, ipPoolUtils, countDownLatch);
            // 当每执行1500次，切换一次ip池
            if (countDownLatch.getCount() % 1500 == 0) {
                ipPoolUtils = new IpPoolUtils();
            }
            if (amazonMeta != null && StringUtils.isNotEmpty(amazonMeta.getAmazonId())) {
                amazonMetaDao.addAmazonMeta(amazonMeta);
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
