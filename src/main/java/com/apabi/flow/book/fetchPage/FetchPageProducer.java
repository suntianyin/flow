package com.apabi.flow.book.fetchPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author: sunty
 * @date: 2018/11/07 14:47
 * @description:
 */

public class FetchPageProducer implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(FetchPageProducer.class);
    private ArrayBlockingQueue idQueue;
    private String threadName;
    private List<String> idList;

    public FetchPageProducer(ArrayBlockingQueue idQueue, String threadName, List<String> idList) {
        this.idQueue = idQueue;
        this.threadName = threadName;
        this.idList = idList;
    }
    @Override
    public void run() {
        logger.info(threadName + "正在添加metaId到生产者队列....");
        Thread.currentThread().setName(threadName);
        for (String id : idList) {
            try {
                idQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
