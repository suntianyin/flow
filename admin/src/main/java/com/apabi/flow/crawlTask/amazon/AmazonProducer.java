package com.apabi.flow.crawlTask.amazon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/17 15:03
 **/
public class AmazonProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AmazonProducer.class);
    private List<String> idList;
    private ArrayBlockingQueue<String> arrayBlockingQueue;
    private String threadName;

    public AmazonProducer(ArrayBlockingQueue arrayBlockingQueue, String threadName, List<String> idList) {
        this.arrayBlockingQueue = arrayBlockingQueue;
        this.threadName = threadName;
        this.idList = idList;
    }

    @Override
    public void run() {
        logger.info(threadName + "正在添加amazonId到生产者队列....");
        Thread.currentThread().setName(threadName);
        for(String id:idList){
            try {
                arrayBlockingQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
