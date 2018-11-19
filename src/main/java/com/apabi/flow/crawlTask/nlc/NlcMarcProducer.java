package com.apabi.flow.crawlTask.nlc;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/12 15:54
 **/
public class NlcMarcProducer implements Runnable {
    private LinkedBlockingQueue<String> isbnQueue;
    private List<String> isbnList;
    private String threadName;

    public NlcMarcProducer(LinkedBlockingQueue<String> isbnQueue, String threadName, List<String> isbnList) {
        this.isbnQueue = isbnQueue;
        this.isbnList = isbnList;
        this.threadName = threadName;
    }

/*    public NlcMarcProducer(List[] isbnList, ArrayBlockingQueue<String> isbnQueue, String threadName){
        this.isbnList = isbnList;
        this.isbnQueue = isbnQueue;
        this.threadName = threadName;
    }*/

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        for (String isbn : isbnList) {
            try {
                isbnQueue.put(isbn);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
