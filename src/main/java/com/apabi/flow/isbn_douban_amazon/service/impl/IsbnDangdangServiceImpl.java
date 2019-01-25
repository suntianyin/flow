package com.apabi.flow.isbn_douban_amazon.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.dangdang.dao.DangdangMetadataDao;
import com.apabi.flow.dangdang.model.DangdangMetadata;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDangdangDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDangdang;
import com.apabi.flow.isbn_douban_amazon.service.IsbnDangdangService;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-18 11:07
 **/
@RequestMapping("isbnDangdang")
@Service
public class IsbnDangdangServiceImpl implements IsbnDangdangService {

    @Autowired
    private IsbnDangdangDao isbnDangdangDao;
    @Autowired
    private DoubanMetaDao doubanMetaDao;
    @Autowired
    private DangdangMetadataDao dangdangMetadataDao;
    @Autowired
    private AmazonMetaDao amazonMetaDao;
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;

    @RequestMapping("crawlDouban")
    @Override
    public void crawlDouban() {
        int count = isbnDangdangDao.countShouldCrawledInDouban();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<IsbnDangdang> isbnDangdangList = isbnDangdangDao.findByPage();
            LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue = new LinkedBlockingQueue<>(isbnDangdangList);
            int listSize = isbnDangdangList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            IsbnDangdangCrawlDoubanConsumer consumer = new IsbnDangdangCrawlDoubanConsumer(isbnDangdangQueue, countDownLatch, nlcIpPoolUtils, doubanMetaDao, isbnDangdangDao);
            for (int j = 0; j < listSize; j++) {
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

    @RequestMapping("crawlAmazon")
    @Override
    public void crawlAmazon() {
        int count = isbnDangdangDao.countShouldCrawledInAmazon();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<IsbnDangdang> isbnDangdangList = isbnDangdangDao.findByPage();
            LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue = new LinkedBlockingQueue<>(isbnDangdangList);
            int listSize = isbnDangdangList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            IsbnDangdangCrawlAmazonConsumer consumer = new IsbnDangdangCrawlAmazonConsumer(isbnDangdangQueue, countDownLatch, nlcIpPoolUtils, amazonMetaDao, isbnDangdangDao);
            for (int j = 0; j < listSize; j++) {
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

    @RequestMapping("crawlNlc")
    @Override
    public void crawlNlc() {
        int count = isbnDangdangDao.countShouldCrawledInNlc();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<IsbnDangdang> isbnDangdangList = isbnDangdangDao.findByPage();
            LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue = new LinkedBlockingQueue<>(isbnDangdangList);
            int listSize = isbnDangdangList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            IsbnDangdangCrawlNlcConsumer consumer = new IsbnDangdangCrawlNlcConsumer(isbnDangdangQueue, countDownLatch, nlcIpPoolUtils, nlcBookMarcDao, isbnDangdangDao);
            for (int j = 0; j < listSize; j++) {
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

    /**
     * 把dangdang中的isbn插入到数据库中
     */
    @RequestMapping("insert")
    public void insertIsbnFromDangdang() {
        int pageSize = 10000;
        int count = dangdangMetadataDao.count();
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<DangdangMetadata> dangdangMetadataList = dangdangMetadataDao.findByPage();
            for (DangdangMetadata dangdangMetadata : dangdangMetadataList) {
                String isbn = dangdangMetadata.getIsbn13();
                if (StringUtils.isEmpty(isbn)) {
                    isbn = dangdangMetadata.getIsbn10();
                }
                IsbnDangdang isbnDangdang = new IsbnDangdang();
                isbnDangdang.setIsbn(isbn);
                isbnDangdang.setAmazonStatus("0");
                isbnDangdang.setDoubanStatus("0");
                isbnDangdang.setNlcStatus("0");
                try {
                    isbnDangdangDao.insert(isbnDangdang);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
