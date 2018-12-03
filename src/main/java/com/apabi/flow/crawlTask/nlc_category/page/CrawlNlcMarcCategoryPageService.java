package com.apabi.flow.crawlTask.nlc_category.page;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc_category.dao.NlcBookMarcCategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/21 14:28
 **/
@Controller
@RequestMapping("/nlcCategory")
public class CrawlNlcMarcCategoryPageService {

    @Autowired
    private NlcBookMarcCategoryDao nlcBookMarcCategoryDao;

    @ResponseBody
    @RequestMapping("/page")
    public String updatePage() {
        List<String> idList = nlcBookMarcCategoryDao.findAllIdListByPageNum();
        LinkedBlockingQueue<String> idQueue = new LinkedBlockingQueue<>(50);
        NlcMarcCategoryPageProducer producer = new NlcMarcCategoryPageProducer(idList, idQueue);
        Thread producerThread = new Thread(producer);
        producerThread.start();
        CountDownLatch countDownLatch = new CountDownLatch(idList.size());
        ExecutorService executorService = Executors.newFixedThreadPool(36);
        NlcIpPoolUtils ipPoolUtils = new NlcIpPoolUtils();
        NlcMarcCategoryPageConsumer consumer = new NlcMarcCategoryPageConsumer(idQueue, countDownLatch, nlcBookMarcCategoryDao, ipPoolUtils);
        for (int i = 0; i < idList.size(); i++) {
            executorService.execute(consumer);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "complete";
    }
}
