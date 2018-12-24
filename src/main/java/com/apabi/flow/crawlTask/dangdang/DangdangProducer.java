package com.apabi.flow.crawlTask.dangdang;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.model.DangdangCrawlUrl;
import com.apabi.flow.dangdang.util.CrawlDangdangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-10 17:42
 **/
public class DangdangProducer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DangdangProducer.class);
    private LinkedBlockingQueue<DangdangCrawlUrl> crawlUrlQueue;
    private LinkedBlockingQueue<String> dangdangUrlQueue;
    private CountDownLatch producerCountDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public DangdangProducer(LinkedBlockingQueue<DangdangCrawlUrl> crawlUrlQueue, LinkedBlockingQueue<String> dangdangUrlQueue, CountDownLatch producerCountDownLatch, NlcIpPoolUtils nlcIpPoolUtils) {
        this.crawlUrlQueue = crawlUrlQueue;
        this.dangdangUrlQueue = dangdangUrlQueue;
        this.producerCountDownLatch = producerCountDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }


    @Override
    public void run() {
        String url = "";
        String ip = "";
        String port = "";
        String time = "";
        List<String> urlList = null;
        try {
            url = crawlUrlQueue.take().getUrl();
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            urlList = CrawlDangdangUtils.crawlDangdangMetaUrlByPage(url, ip, port);
            for (String urlContent : urlList) {
                dangdangUrlQueue.put(urlContent);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = simpleDateFormat.format(new Date());
            if (urlList != null) {
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "；列表大小为：" + urlList.size() + "；剩余列表数：" + producerCountDownLatch.getCount());
            } else {
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "失败；剩余列表数：" + producerCountDownLatch.getCount());
            }
        } catch (Exception e) {
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "失败；剩余列表数：" + producerCountDownLatch.getCount());
        } finally {
            producerCountDownLatch.countDown();
        }
    }
}
