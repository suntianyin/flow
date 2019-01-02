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
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonIdProducer.class);
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
        long startTime = System.currentTimeMillis();
        String ip = "";
        String port = "";
        try {
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            List<String> amazonIdList = CrawlAmazonUtils.crawlAmazonIdList(url, ip,port);
            for (String id : amazonIdList) {
                idList.add(id);
            }
            long endTime = System.currentTimeMillis();
            LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "；列表大小为：" + amazonIdList.size() + "；剩余列表数：" + countDownLatch.getCount() + "；已经抓取的id数量：" + idList.size() + "；耗时为：" + (endTime - startTime) / 1000 + "秒");
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
