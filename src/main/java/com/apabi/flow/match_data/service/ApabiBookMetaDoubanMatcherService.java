package com.apabi.flow.match_data.service;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDoubanAmazonDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDoubanAmazon;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.match_data.dao.ApabiBookMetaDoubanMatcherDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-2 13:49
 **/
@RestController
@RequestMapping("apabiDoubanMatcher")
public class ApabiBookMetaDoubanMatcherService {

    @Autowired
    private DoubanMetaDao doubanMetaDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaDoubanMatcherDao apabiBookMetaDoubanMatcherDao;
    @Autowired
    private JdMetadataDao jdMetadataDao;
    @Autowired
    private IsbnDoubanAmazonDao isbnDoubanAmazonDao;
    @Autowired
    private AmazonMetaDao amazonMetaDao;
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;


    @RequestMapping("matchIsbn13")
    public String matchIsbn13() {
        int pageSize = 100000;
        int total = doubanMetaDao.count();
        int pageNum = (total / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 1; i <= pageNum; i++) {
            long start = System.currentTimeMillis();
            PageHelper.startPage(i, pageSize);
            Page<DoubanMeta> doubanMetaList = doubanMetaDao.findByPageOrderByDoubanId();
            //Page<DoubanMeta> doubanMetaList = new Page<>();
            //doubanMetaList.add(doubanMetaDao.findById("26992957"));
            int listSize = doubanMetaList.size();
            LinkedBlockingQueue<DoubanMeta> doubanMetaQueue = new LinkedBlockingQueue<>(doubanMetaList);
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            ApabiBookMetaDoubanMatcherIsbn13Consumer consumer = new ApabiBookMetaDoubanMatcherIsbn13Consumer(doubanMetaQueue, countDownLatch, apabiBookMetaDataDao, apabiBookMetaDoubanMatcherDao, i);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("处理第" + (i - 1) * 100000 + "条到第" + i * 100000 + "条数据耗时" + (end - start) / 1000 + "s");
        }
        return "success";
    }

    @RequestMapping("matchIsbn10")
    public String matchIsbn10() {
        int pageSize = 100000;
        int total = doubanMetaDao.count();
        int pageNum = (total / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 1; i <= pageNum; i++) {
            long start = System.currentTimeMillis();
            PageHelper.startPage(i, pageSize);
            Page<DoubanMeta> doubanMetaList = doubanMetaDao.findByPageOrderByDoubanId();
            int listSize = doubanMetaList.size();
            LinkedBlockingQueue<DoubanMeta> doubanMetaQueue = new LinkedBlockingQueue<>(doubanMetaList);
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            ApabiBookMetaDoubanMatcherIsbn10Consumer consumer = new ApabiBookMetaDoubanMatcherIsbn10Consumer(doubanMetaQueue, countDownLatch, apabiBookMetaDataDao, apabiBookMetaDoubanMatcherDao, i);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("处理第" + (i - 1) * 10000 + "条到第" + i * 10000 + "条数据耗时" + (end - start) / 1000 + "s");
        }
        return "success";
    }

    @RequestMapping("matchJd")
    public String matchJd() {
        int count = jdMetadataDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        int hitCount = 0;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<String> isbn13List = jdMetadataDao.findAllIsbn13ByPage();
            System.out.println("正在处理第" + (i - 1) * pageSize + "条到第" + i * pageSize + "条");
            for (String isbn13 : isbn13List) {
                if (isbn13 != null) {
                    String isbn13Value = isbn13.replaceAll("-", "");
                    List<DoubanMeta> doubanMetaList = doubanMetaDao.findByIsbn13(isbn13Value);
                    if (doubanMetaList != null && doubanMetaList.size() == 1) {
                        System.out.println("匹配上的isbn13为" + isbn13);
                        hitCount++;
                    }
                }
            }
        }
        System.out.println("匹配上数量为：" + hitCount);
        return "success";
    }

    @RequestMapping("notMatchJd")
    public String notMatchJd() {
        int count = jdMetadataDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        int hitCount = 0;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<String> isbn13List = jdMetadataDao.findAllIsbn13ByPage();
            System.out.println("正在处理第" + (i - 1) * pageSize + "条到第" + i * pageSize + "条");
            for (String isbn13 : isbn13List) {
                if (isbn13 != null) {
                    String isbn13Value = isbn13.replaceAll("-", "");
                    List<DoubanMeta> doubanMetaList = doubanMetaDao.findByIsbn13(isbn13Value);
                    if (doubanMetaList == null || doubanMetaList.size() == 0) {
                        IsbnDoubanAmazon isbnDoubanAmazon = new IsbnDoubanAmazon();
                        isbnDoubanAmazon.setIsbn(isbn13Value);
                        isbnDoubanAmazon.setDoubanStatus("0");
                        isbnDoubanAmazon.setAmazonStatus("0");
                        try {
                            isbnDoubanAmazonDao.insert(isbnDoubanAmazon);
                            System.out.println("插入的isbn13为" + isbn13);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hitCount++;
                    }
                }
            }
        }
        System.out.println("匹配上数量为：" + hitCount);
        return "success";
    }

    /**
     * 分别在douban，amazon和nlc中，抓取在jd中存在，douban中不存在的isbn
     *
     * @return
     */
    @RequestMapping("crawlInJdNotInDouban")
    public String crawlInJdNotInDouban() {
        int count = isbnDoubanAmazonDao.countInJdNotInDouban();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<IsbnDoubanAmazon> isbnList = isbnDoubanAmazonDao.findInJdNotInDoubanByPage();
            LinkedBlockingQueue<IsbnDoubanAmazon> isbnQueue = new LinkedBlockingQueue<>(isbnList);
            int listSize = isbnList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            CrawlDoubanAmazonNlcInJdNotInDoubanConsumer consumer = new CrawlDoubanAmazonNlcInJdNotInDoubanConsumer(isbnQueue, countDownLatch, isbnDoubanAmazonDao, doubanMetaDao, amazonMetaDao, nlcBookMarcDao, nlcIpPoolUtils);
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
        return "success";
    }
}
