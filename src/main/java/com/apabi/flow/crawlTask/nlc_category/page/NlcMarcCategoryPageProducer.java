package com.apabi.flow.crawlTask.nlc_category.page;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * nlc分类id入队列
 *
 * @Author pipi
 * @Date 2018/11/21 14:28
 **/
public class NlcMarcCategoryPageProducer implements Runnable {

    private List<String> idList;
    private LinkedBlockingQueue<String> idQueue;

    public NlcMarcCategoryPageProducer(List<String> idList, LinkedBlockingQueue<String> idQueue) {
        this.idList = idList;
        this.idQueue = idQueue;
    }

    @Override
    public void run() {
        for (String id : idList) {
            try {
                idQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
