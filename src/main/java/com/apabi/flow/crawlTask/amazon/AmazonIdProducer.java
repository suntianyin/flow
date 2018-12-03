package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/23 14:23
 **/
public class AmazonIdProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AmazonIdProducer.class);
    private IpPoolUtils ipPoolUtils;
    private List<String> idList;
    private String url;
    private CountDownLatch countDownLatch;

    public AmazonIdProducer(String url, List<String> idList, CountDownLatch countDownLatch, IpPoolUtils ipPoolUtils) {
        this.url = url;
        this.idList = idList;
        this.countDownLatch = countDownLatch;
        this.ipPoolUtils = ipPoolUtils;
    }

    @Override
    public void run() {
        try {
            List<String> amazonIdList = CrawlAmazonUtils.crawlAmazonIdList(url, ipPoolUtils, countDownLatch);
            for (String id : amazonIdList) {
                idList.add(id);
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
