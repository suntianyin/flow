package com.apabi.flow.xinhuashudaun.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianCrawlPageUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianCrawlUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianItemUrlDao;
import com.apabi.flow.xinhuashudaun.dao.XinhuashudianMetadataDao;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlPageUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianCrawlUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-14 14:18
 **/
@RestController
@RequestMapping("xhsd")
public class XinhuashudianMetadataServiceImpl {
    @Autowired
    private XinhuashudianMetadataDao xinhuashudianMetadataDao;

    @Autowired
    private XinhuashudianCrawlUrlDao xinhuashudianCrawlUrlDao;

    @Autowired
    private XinhuashudianCrawlPageUrlDao xinhuashudianCrawlPageUrlDao;

    @Autowired
    private XinhuashudianItemUrlDao xinhuashudianItemUrlDao;

    /**
     * 从文本文件中解析出crawlUrl插入到数据库
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("insert")
    public String insertUrl() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\xhsd.txt"));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            XinhuashudianCrawlUrl xinhuashudianCrawlUrl = new XinhuashudianCrawlUrl();
            String[] split = line.split("\t");
            String description = split[0];
            String url = split[1];
            String pageNum = split[2];
            String status = split[3];
            xinhuashudianCrawlUrl.setDescription(description);
            xinhuashudianCrawlUrl.setUrl(url);
            xinhuashudianCrawlUrl.setPageNum(pageNum);
            xinhuashudianCrawlUrl.setStatus(status);
            xinhuashudianCrawlUrlDao.insert(xinhuashudianCrawlUrl);
        }
        return "success";
    }

    /**
     * 插入分页url
     *
     * @return
     */
    @RequestMapping("insertPageUrl")
    public String insertPageUrl() {
        List<XinhuashudianCrawlUrl> xinhuashudianCrawlUrlList = xinhuashudianCrawlUrlDao.findAll();
        for (XinhuashudianCrawlUrl xinhuashudianCrawlUrl : xinhuashudianCrawlUrlList) {
            for (int i = 1; i <= 100; i++) {
                String url = xinhuashudianCrawlUrl.getUrl();
                url = url + "&pageNo=" + i;
                XinhuashudianCrawlPageUrl xinhuashudianCrawlPageUrl = new XinhuashudianCrawlPageUrl();
                xinhuashudianCrawlPageUrl.setStatus("0");
                xinhuashudianCrawlPageUrl.setUrl(url);
                xinhuashudianCrawlPageUrlDao.insert(xinhuashudianCrawlPageUrl);
            }
        }
        return "success";
    }

    /**
     * 抓取itemUrl
     *
     * @return
     */
    @RequestMapping("crawlItemUrl")
    public String insertItemUrl() {
        List<XinhuashudianCrawlPageUrl> xinhuashudianCrawlPageUrlList = xinhuashudianCrawlPageUrlDao.findByPageWithoutCrawled();
        LinkedBlockingQueue<XinhuashudianCrawlPageUrl> pageUrlQueue = new LinkedBlockingQueue<>(xinhuashudianCrawlPageUrlList);
        int pageUrlQueueSize = pageUrlQueue.size();
        CountDownLatch pageUrlCountDownLatch = new CountDownLatch(pageUrlQueueSize);
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        XinhuashudianCrawlPageUrlConsumer consumer = new XinhuashudianCrawlPageUrlConsumer(pageUrlQueue, nlcIpPoolUtils, pageUrlCountDownLatch, xinhuashudianItemUrlDao,xinhuashudianCrawlPageUrlDao);
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (int i = 0; i < pageUrlQueueSize; i++) {
            executorService.execute(consumer);
        }
        try {
            pageUrlCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        return "success";
    }

    @RequestMapping("crawlItem")
    public String crawl(){
        int count = xinhuashudianItemUrlDao.countWithoutCrawled();
        int pageSize = 10000;
        int pageNum = (count/pageSize)+1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<XinhuashudianItemUrl> xinhuashudianItemUrlList = xinhuashudianItemUrlDao.findWithoutCrawled();
            int queueSize = xinhuashudianItemUrlList.size();
            CountDownLatch countDownLatch = new CountDownLatch(queueSize);
            LinkedBlockingQueue<XinhuashudianItemUrl> xinhuashudianItemUrlQueue = new LinkedBlockingQueue<>(xinhuashudianItemUrlList);
            XinhuashudianItemUrlConsumer consumer = new XinhuashudianItemUrlConsumer(xinhuashudianItemUrlQueue, nlcIpPoolUtils, countDownLatch, xinhuashudianMetadataDao, xinhuashudianItemUrlDao);
            for (int j = 0; j < queueSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return "success";
    }
}
