package com.apabi.flow.newspaper.task;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 多线程抓取报纸生产者
 *
 * @Author pipi
 * @Date 2018/11/7 14:26
 **/
public class CrawlHtmlContentProducer implements Runnable {

    private List<String> urlList;
    private ArrayBlockingQueue<String> urlQueue;

    public CrawlHtmlContentProducer(List<String> urlList, ArrayBlockingQueue<String> urlQueue) {
        this.urlList = urlList;
        this.urlQueue = urlQueue;
    }

    /**
     * 开启一个生产者线程，将urlList中的数据持续向阻塞队列中添加
     */
    @Override
    public void run() {
        for (int i = 0; i < urlList.size(); i++) {
            try {
                urlQueue.put(urlList.get(i));
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
