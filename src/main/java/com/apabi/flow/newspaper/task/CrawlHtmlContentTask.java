package com.apabi.flow.newspaper.task;

import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
 * @Date 2018/11/7 14:08
 **/
@Controller
@RequestMapping("/crawlHtml")
public class CrawlHtmlContentTask {

    @Autowired
    private NewspaperDao newspaperDao;


    @ResponseBody
    @RequestMapping("/execute")
    public String updateNoHtml() {
        int total = newspaperDao.countNoHtmlContent();
        int pageSize = 10000;
        int pageNum = (total / pageSize) + 1;
        ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(100);
        ExecutorService executorService = Executors.newFixedThreadPool(800);
        for (int i = 0; i < pageNum; i++) {
            CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<String> urlList = newspaperDao.findNoHtmlContentUrlsByPage();
            CountDownLatch countDownLatch = new CountDownLatch(urlList.size());
            CrawlHtmlContentProducer producer = new CrawlHtmlContentProducer(urlList, urlQueue);
            new Thread(producer).start();
            CrawlHtmlContentConsumer consumer = new CrawlHtmlContentConsumer(urlQueue, countDownLatch, cnrIpPoolUtils, newspaperDao);
            for (int j = 0; j < urlList.size(); j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "complete";
    }

}
