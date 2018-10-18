package com.apabi.flow.ip;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author pipi
 * @Date 2018/9/18 17:32
 **/
public class Consumer implements Runnable {
    private Lock lock = new ReentrantLock();
    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            lock.lock();
            while (queue.size() > 0) {
                // 获取ip地址和端口号
                String address = queue.take();
                String[] info = address.split(",");
                String ip = info[0];
                Integer.parseInt(info[1]);


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
