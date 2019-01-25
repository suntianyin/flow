package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.match_data.dao.ApabiBookMetaNlcMatcherDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-7 17:39
 **/
@Controller
@RequestMapping("apabiNlcMatcher")
public class ApabiBookMetaNlcMatcherService {
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;
    @Autowired
    private ApabiBookMetaNlcMatcherDao apabiBookMetaNlcMatcherDao;

    @RequestMapping("match")
    public String matchIsbn() {
        int count = nlcBookMarcDao.getTotalCount();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<NlcBookMarc> nlcBookMarcList = nlcBookMarcDao.findByPage();
            int listSize = nlcBookMarcList.size();
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            LinkedBlockingQueue<NlcBookMarc> marcQueue = new LinkedBlockingQueue<>(nlcBookMarcList);
            ApabiBookMetaNlcMatcherIsbnConsumer consumer = new ApabiBookMetaNlcMatcherIsbnConsumer(marcQueue, countDownLatch, apabiBookMetaDataDao, apabiBookMetaNlcMatcherDao, i);
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