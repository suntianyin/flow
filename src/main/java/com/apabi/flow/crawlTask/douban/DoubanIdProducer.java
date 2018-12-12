package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/22 14:52
 **/
public class DoubanIdProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubanIdProducer.class);
    private String url;
    private List<String> idList;
    private CountDownLatch countDownLatch;
    private IpPoolUtils ipPoolUtils;

    public DoubanIdProducer(String url, List<String> idList, CountDownLatch countDownLatch, IpPoolUtils ipPoolUtils) {
        this.url = url;
        this.idList = idList;
        this.countDownLatch = countDownLatch;
        this.ipPoolUtils = ipPoolUtils;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        // 随机指定代理ip抓取doubanId
        String ip = "";
        String port = "";
        String host = ipPoolUtils.getIp();
        ip = host.split(":")[0];
        port = host.split(":")[1];
        List<String> doubanIdList = new ArrayList<>();
        doubanIdList = CrawlDoubanUtil.crawlDoubanIdList(url, ip, port);
        long endTime = System.currentTimeMillis();
        LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "；列表大小为：" + doubanIdList.size() + "；剩余列表数：" + countDownLatch.getCount() + "；已经抓取的id数量：" + idList.size() + "；耗时为：" + (endTime - startTime) / 1000 + "秒");
        for (String id : doubanIdList) {
            idList.add(id);
        }
        countDownLatch.countDown();
    }
}
