package com.apabi.flow.crawlTask.xinhuashudian;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianMetadataDao;
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
 * @Date 2018-12-19 14:11
 **/
public class XinhuashudianConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(XinhuashudianConsumer.class);
    private LinkedBlockingQueue<String> itemUrlQueue;
    private XinhuashudianMetadataDao xinhuashudianMetadataDao;
    private CountDownLatch itemUrlCountDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;

    public XinhuashudianConsumer(LinkedBlockingQueue<String> itemUrlQueue, XinhuashudianMetadataDao xinhuashudianMetadataDao, CountDownLatch itemUrlCountDownLatch, NlcIpPoolUtils nlcIpPoolUtils) {
        this.itemUrlCountDownLatch = itemUrlCountDownLatch;
        this.itemUrlQueue = itemUrlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.xinhuashudianMetadataDao = xinhuashudianMetadataDao;
    }

    @Override
    public void run() {
        String ip = "";
        String port = "";
        String url = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            url = itemUrlQueue.take();
            XinhuashudianMetadata xinhuashudianMetadata = CrawlXinhuashudianMetadataUtil.crawlByUrl(url, ip, port);
            try {
                xinhuashudianMetadataDao.insert(xinhuashudianMetadata);
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在xinhuashudian抓取" + url + "并添加至数据库成功，列表中剩余：" + itemUrlCountDownLatch.getCount() + "个数据...");
            } catch (Exception e) {
            }
        } catch (Exception e) {
        } finally {
            itemUrlCountDownLatch.countDown();
        }
    }
}
