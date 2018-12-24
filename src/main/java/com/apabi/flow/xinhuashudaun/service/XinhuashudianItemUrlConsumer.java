package com.apabi.flow.xinhuashudaun.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianItemUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianMetadataDao;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianMetadata;
import com.apabi.flow.xinhuashudaun.util.CrawlXinhuashudianMetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-19 11:30
 **/
public class XinhuashudianItemUrlConsumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(XinhuashudianItemUrlConsumer.class);
    private LinkedBlockingQueue<XinhuashudianItemUrl> xinhuashudianItemUrlQueue;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CountDownLatch countDownLatch;
    private XinhuashudianMetadataDao xinhuashudianMetadataDao;
    private XinhuashudianItemUrlDao xinhuashudianItemUrlDao;

    public XinhuashudianItemUrlConsumer(LinkedBlockingQueue<XinhuashudianItemUrl> xinhuashudianItemUrlQueue, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch countDownLatch, XinhuashudianMetadataDao xinhuashudianMetadataDao, XinhuashudianItemUrlDao xinhuashudianItemUrlDao) {
        this.xinhuashudianItemUrlQueue = xinhuashudianItemUrlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.countDownLatch = countDownLatch;
        this.xinhuashudianMetadataDao = xinhuashudianMetadataDao;
        this.xinhuashudianItemUrlDao = xinhuashudianItemUrlDao;
    }

    @Override
    public void run() {
        String url = "";
        String ip = "";
        String port = "";
        XinhuashudianItemUrl xinhuashudianItemUrl = null;
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            xinhuashudianItemUrl = xinhuashudianItemUrlQueue.take();
            url = xinhuashudianItemUrl.getUrl();
            XinhuashudianMetadata xinhuashudianMetadata = CrawlXinhuashudianMetadataUtil.crawlByUrl(url, ip, port);
            if (xinhuashudianMetadata != null) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在xinhuashudian抓取" + url + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                xinhuashudianItemUrlDao.updateHasCrawled(url);
                try {
                    xinhuashudianMetadataDao.insert(xinhuashudianMetadata);
                } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在xinhuashudian抓取" + url + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
        } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在xinhuashudian抓取" + url + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");

        } finally {
            countDownLatch.countDown();
        }
    }
}
