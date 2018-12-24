package com.apabi.flow.xinhuashudaun.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianCrawlPageUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianItemUrlDao;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlPageUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.apabi.flow.xinhuashudaun.util.CrawlXinhuashudianMetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-18 17:42
 **/
public class XinhuashudianCrawlPageUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(XinhuashudianCrawlPageUrlConsumer.class);
    private LinkedBlockingQueue<XinhuashudianCrawlPageUrl> pageUrlQueue;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CountDownLatch pageUrlCountDownLatch;
    private XinhuashudianItemUrlDao xinhuashudianItemUrlDao;
    private XinhuashudianCrawlPageUrlDao xinhuashudianCrawlPageUrlDao;

    public XinhuashudianCrawlPageUrlConsumer(LinkedBlockingQueue<XinhuashudianCrawlPageUrl> pageUrlQueue, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch pageUrlCountDownLatch, XinhuashudianItemUrlDao xinhuashudianItemUrlDao,XinhuashudianCrawlPageUrlDao xinhuashudianCrawlPageUrlDao) {
        this.pageUrlQueue = pageUrlQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.pageUrlCountDownLatch = pageUrlCountDownLatch;
        this.xinhuashudianItemUrlDao = xinhuashudianItemUrlDao;
        this.xinhuashudianCrawlPageUrlDao = xinhuashudianCrawlPageUrlDao;
    }

    @Override
    public void run() {
        String url = "";
        String ip = "";
        String port = "";
        XinhuashudianCrawlPageUrl xinhuashudianCrawlPageUrl = null;
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            xinhuashudianCrawlPageUrl = pageUrlQueue.take();
            url = xinhuashudianCrawlPageUrl.getUrl();
            List<XinhuashudianItemUrl> xinhuashudianItemUrlList = CrawlXinhuashudianMetadataUtil.crawlXinhuashudianItemUrlListByUrl(url, ip, port);
            LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在xinhuashudian抓取" + url + "并添加至数据库成功，列表中剩余：" + pageUrlCountDownLatch.getCount() + "个数据...");
            xinhuashudianCrawlPageUrlDao.updateHasCrawled(url);
            for (XinhuashudianItemUrl xinhuashudianItemUrl : xinhuashudianItemUrlList) {
                try {
                    xinhuashudianItemUrlDao.insert(xinhuashudianItemUrl);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {

        } finally {
            pageUrlCountDownLatch.countDown();
        }
    }
}
