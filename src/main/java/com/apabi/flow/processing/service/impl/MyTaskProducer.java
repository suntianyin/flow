package com.apabi.flow.processing.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class MyTaskProducer implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(MyTaskProducer.class);
    private ArrayBlockingQueue<File> idQueue;
    private List<File> idList;

    public MyTaskProducer(ArrayBlockingQueue<File> idQueue, List<File> idList) {
        this.idQueue = idQueue;
        this.idList = idList;
    }

    @Override
    public void run() {
        for (File file : idList) {
            try {
                idQueue.put(file);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
