package com.apabi.flow.isbn_douban_amazon.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDoubanAmazonDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDoubanAmazon;
import com.apabi.flow.isbn_douban_amazon.service.IsbnDoubanAmazonService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-12 14:22
 **/
@Service
public class IsbnDoubanAmazonServiceImpl implements IsbnDoubanAmazonService {

    @Autowired
    private IsbnDoubanAmazonDao isbnDoubanAmazonDao;

    @Autowired
    private DoubanMetaDao doubanMetaDao;

    @Autowired
    private AmazonMetaDao amazonMetaDao;

    /**
     * 根据isbn在douban和amazon上抓取数据
     */
    @Override
    public void crawl() {
        int count = isbnDoubanAmazonDao.countWithoutCrawled();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<IsbnDoubanAmazon> isbnDoubanAmazonList = isbnDoubanAmazonDao.findAllWithoutCrawledByPage();
            LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue = new LinkedBlockingQueue<>(isbnDoubanAmazonList);
            int queueSize = isbnDoubanAmazonList.size();
            CountDownLatch countDownLatch = new CountDownLatch(queueSize);
            IsbnDoubanAmazonConsumer consumer = new IsbnDoubanAmazonConsumer(isbnQueue, countDownLatch, nlcIpPoolUtils, doubanMetaDao, amazonMetaDao, isbnDoubanAmazonDao);
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
    }
}
