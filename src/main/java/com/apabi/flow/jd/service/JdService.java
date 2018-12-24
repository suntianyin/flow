package com.apabi.flow.jd.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.dao.JdCrawlPageUrlDao;
import com.apabi.flow.jd.dao.JdCrawlUrlDao;
import com.apabi.flow.jd.dao.JdItemUrlDao;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.jd.model.JdCrawlPageUrl;
import com.apabi.flow.jd.model.JdCrawlUrl;
import com.apabi.flow.jd.model.JdItemUrl;
import com.apabi.flow.jd.util.CrawlJdUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-4 14:18
 **/
@RestController
@RequestMapping("jd")
public class JdService {
    /**
     * CPU核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    @Autowired
    private JdCrawlUrlDao jdCrawlUrlDao;

    @Autowired
    private JdCrawlPageUrlDao jdCrawlPageUrlDao;

    @Autowired
    private JdItemUrlDao jdItemUrlDao;

    @Autowired
    private JdMetadataDao jdMetadataDao;

    /**
     * 将分类首页添加到数据库
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("insertIndexUrl")
    public String insertIndexUrl() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\jd.txt"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            String url = line.split("\t")[1];
            String description = line.split("\t")[0];
            JdCrawlUrl jdCrawlUrl = new JdCrawlUrl();
            jdCrawlUrl.setDescription(description);
            jdCrawlUrl.setUrl(url);
            jdCrawlUrl.setPageNum("0");
            jdCrawlUrl.setStatus("0");
            jdCrawlUrlDao.insert(jdCrawlUrl);
        }
        return "success";
    }

    /**
     * 更新分类的页数
     *
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("updatePageNum")
    public String updatePageNum() throws InterruptedException {
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        List<JdCrawlUrl> jdCrawlUrlList = jdCrawlUrlDao.findAllWithNoPage();
        for (JdCrawlUrl jdCrawlUrl : jdCrawlUrlList) {
            Thread.sleep(1000);
            int pageNum = CrawlJdUtils.crawlItemUrlPageNumByPageUrl(jdCrawlUrl.getUrl(), nlcIpPoolUtils);
            jdCrawlUrl.setPageNum(pageNum + "");
            jdCrawlUrlDao.update(jdCrawlUrl);
        }
        return "success";
    }

    /**
     * 创建分页种子URL
     *
     * @return
     */
    @RequestMapping("insertPageUrl")
    public String insertPageUrl() {
        int count = 0;
        List<JdCrawlUrl> urlList = jdCrawlUrlDao.findAll();
        for (JdCrawlUrl jdCrawlUrl : urlList) {
            int pageNum = Integer.parseInt(jdCrawlUrl.getPageNum());
            String indexUrl = jdCrawlUrl.getUrl();
            for (int i = 1; i <= pageNum; i++) {
                String pageUrl = indexUrl.replace("page=1", "page=" + i);
                JdCrawlPageUrl jdCrawlPageUrl = new JdCrawlPageUrl();
                jdCrawlPageUrl.setStatus("0");
                jdCrawlPageUrl.setUrl(pageUrl);
                jdCrawlPageUrlDao.insert(jdCrawlPageUrl);
            }
        }
        System.out.println(count);
        return "success";
    }

    /**
     * 抓取每个页面上item的url
     *
     * @return
     */
    @RequestMapping("crawlItemUrl")
    public String crawlItemUrl() {
        List<JdCrawlPageUrl> jdCrawlPageUrlList = jdCrawlPageUrlDao.findAllWithoutCrawled();
        LinkedBlockingQueue<JdCrawlPageUrl> urlQueue = new LinkedBlockingQueue<JdCrawlPageUrl>(jdCrawlPageUrlList);
        NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
        int urlQueueSize = urlQueue.size();
        CountDownLatch countDownLatch = new CountDownLatch(urlQueueSize);
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_COUNT * 20);
        JdPageUrlConsumer consumer = new JdPageUrlConsumer(urlQueue, countDownLatch, jdCrawlPageUrlDao, jdItemUrlDao, nlcIpPoolUtils);
        for (int i = 0; i < urlQueueSize; i++) {
            executorService.execute(consumer);
        }
        executorService.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }


    @RequestMapping("crawl")
    public String crawlJdMetadataByUrl() {
        int count = jdItemUrlDao.countWithoutCrawled();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<JdItemUrl> jdItemUrls = jdItemUrlDao.findByPageWithoutCrawled();
            List<String> urlList = new ArrayList<>();
            LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue(1000);
            for (JdItemUrl jdItemUrl : jdItemUrls) {
                String url = "https:" + jdItemUrl.getUrl();
                urlList.add(url);
            }
            int countSize = urlList.size();
            // 创建Jd生产者
            JdMetadataProducer producer = new JdMetadataProducer(urlList, urlQueue);
            new Thread(producer).start();
            // 创建Jd消费者
            CountDownLatch countDownLatch = new CountDownLatch(countSize);
            JdMetadataConsumer consumer = new JdMetadataConsumer(urlQueue, nlcIpPoolUtils, countDownLatch,jdMetadataDao,jdItemUrlDao);
            for (int j = 0; j < pageSize; j++) {
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
