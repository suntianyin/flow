package com.apabi.flow.newspaper.chinadaily.task;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/14 17:31
 **/
public class CrawlChinaDailyProducer implements Runnable {
    private List<String> urlList;
    private LinkedBlockingQueue<String> urlQueue;

    public CrawlChinaDailyProducer(List<String> urlList,LinkedBlockingQueue<String> urlQueue){
        this.urlList = urlList;
        this.urlQueue = urlQueue;
    }

    @Override
    public void run() {
        for (String url : urlList) {
            try {
                urlQueue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
