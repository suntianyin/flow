package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.match_data.dao.ApabiBookMetaDoubanMatcherDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-2 13:49
 **/
@RestController
@RequestMapping("apabiDouban")
public class ApabiBookMetaDoubanMatcherService {

    @Autowired
    private DoubanMetaDao doubanMetaDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaDoubanMatcherDao apabiBookMetaDoubanMatcherDao;

    @RequestMapping("match")
    public String match() {
        int pageSize = 10000;
        Page<DoubanMeta> doubanMetaList = doubanMetaDao.findByPage(null);
        return "success";
    }

    @RequestMapping("matchIsbn13")
    public String matchIsbn13() {
        int pageSize = 10000;
        int total = doubanMetaDao.count();
        int pageNum = (total / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<DoubanMeta> doubanMetaList = doubanMetaDao.findByPageOrderByDoubanId();
            int listSize = doubanMetaList.size();
            LinkedBlockingQueue<DoubanMeta> doubanMetaQueue = new LinkedBlockingQueue<>(doubanMetaList);
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            ApabiBookMetaDoubanMatcherIsbn13Consumer consumer = new ApabiBookMetaDoubanMatcherIsbn13Consumer(doubanMetaQueue,countDownLatch,apabiBookMetaDataDao,apabiBookMetaDoubanMatcherDao,i);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
}
