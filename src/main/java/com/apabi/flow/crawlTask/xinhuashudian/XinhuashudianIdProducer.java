package com.apabi.flow.crawlTask.xinhuashudian;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.apabi.flow.xinhuashudaun.util.CrawlXinhuashudianMetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-19 14:12
 **/
public class XinhuashudianIdProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(XinhuashudianIdProducer.class);
    private LinkedBlockingQueue<String> itemUrlQueue;
    private LinkedBlockingQueue<XinhuashudianCrawlUrl> xinhuashudianCrawlUrlQueue;
    private CountDownLatch crawlUrlCountDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public XinhuashudianIdProducer(LinkedBlockingQueue<String> itemUrlQueue, LinkedBlockingQueue<XinhuashudianCrawlUrl> xinhuashudianCrawlUrlQueue, CountDownLatch crawlUrlCountDownLatch, NlcIpPoolUtils nlcIpPoolUtils) {
        this.itemUrlQueue = itemUrlQueue;
        this.xinhuashudianCrawlUrlQueue = xinhuashudianCrawlUrlQueue;
        this.crawlUrlCountDownLatch = crawlUrlCountDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
    }

    @Override
    public void run() {
        String ip ="";
        String port = "";
        String url = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip  = host.split(":")[0];
            port = host.split(":")[1];
            url = xinhuashudianCrawlUrlQueue.take().getUrl();
            List<XinhuashudianItemUrl> xinhuashudianItemUrlList = CrawlXinhuashudianMetadataUtil.crawlXinhuashudianItemUrlListByUrl(url, ip, port);
            for (XinhuashudianItemUrl xinhuashudianItemUrl : xinhuashudianItemUrlList) {
                itemUrlQueue.put(xinhuashudianItemUrl.getUrl());
            }
        }catch (Exception e){

        }finally {
            crawlUrlCountDownLatch.countDown();
        }
    }
}
