package com.apabi.flow.crawlTask.nlc;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/12 15:54
 **/
public class NlcMarcProducer implements Runnable {
    private ArrayBlockingQueue<String> isbnQueue;
    private String[] isbnList;
    //    private List[] isbnList;
    private String threadName;

    public NlcMarcProducer(ArrayBlockingQueue<String> isbnQueue, String threadName, String[] isbnList) {
        this.isbnQueue = isbnQueue;
        this.isbnList = isbnList;
        this.threadName = threadName;
    }

    /*public NlcMarcProducer(List[] isbnList, ArrayBlockingQueue<String> isbnQueue, String threadName){
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
