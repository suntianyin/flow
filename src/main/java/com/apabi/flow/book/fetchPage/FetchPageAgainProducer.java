package com.apabi.flow.book.fetchPage;

import com.apabi.flow.book.model.PageCrawledTemp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author: sunty
 * @date: 2018/11/07 14:47
 * @description:
 */

public class FetchPageAgainProducer implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(FetchPageAgainProducer.class);
    private ArrayBlockingQueue<PageCrawledTemp> idQueue;
    private String threadName;
    private List<PageCrawledTemp> idList;

    public FetchPageAgainProducer(ArrayBlockingQueue<PageCrawledTemp> idQueue, String threadName, List<PageCrawledTemp> idList) {
        this.idQueue = idQueue;
        this.threadName = threadName;
        this.idList = idList;
    }
    @Override
    public void run() {
        logger.info(threadName + "正在添加metaId到生产者队列....");
        Thread.currentThread().setName(threadName);
        for (PageCrawledTemp pageCrawledTemp : idList) {
            try {
                idQueue.put(pageCrawledTemp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
