package com.apabi.flow.crawlTask.douban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/16 16:14
 **/
public class DoubanProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DoubanProducer.class);
    private LinkedBlockingQueue idQueue;
    private String threadName;
    private List<String> idList;

    public DoubanProducer(LinkedBlockingQueue idQueue, String threadName, List<String> idList) {
        this.idQueue = idQueue;
        this.threadName = threadName;
        this.idList = idList;
    }

    @Override
    public void run() {
        logger.info(threadName + "正在添加doubanId到生产者队列....");
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
