package com.apabi.flow.newspaper.cnr.task;

import com.apabi.flow.newspaper.cnr.util.CnrCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author pipi
 * @Date 2018/11/2 11:13
 **/
@Controller
@RequestMapping("/cnr")
public class CrawlCnrService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlCnrService.class);
    @Autowired
    private NewspaperDao newspaperDao;

    @RequestMapping("/crawl")
    @ResponseBody
    public String crawl() {
        LOGGER.info("CNR报纸抓取开始...");
        long startTime = System.currentTimeMillis();
        // 创建ip池对象
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        // 获取cnr页码
        int pageNum = CnrCrawlUtils.getPageNum("http://news.cnr.cn/native/");
        CloseableHttpClient httpClient = CnrCrawlUtils.getCloseableHttpClient();
        CountDownLatch countDownLatch = new CountDownLatch(pageNum);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(100);
        for (int i = 1; i <= pageNum; i++) {
            String url = "http://news.cnr.cn/native/index_" + i + ".html";
            try {
                urlQueue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        CrawlCnrTask crawlCnrTask = new CrawlCnrTask(urlQueue, newspaperDao, countDownLatch, cnrIpPoolUtils, httpClient);
        for (int i = 0; i < pageNum; i++) {
            executorService.execute(crawlCnrTask);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("CNR报纸抓取结束...耗时为：" + (endTime - startTime) / 1000 + "秒");
        return "complete";
    }
}
