package com.apabi.flow.jd.service;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-12-5 16:00
 **/
public class JdMetadataProducer implements Runnable {
    private List<String> urlList;
    private LinkedBlockingQueue<String> urlQueue;

    public JdMetadataProducer(List<String> urlList, LinkedBlockingQueue<String> urlQueue) {
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
