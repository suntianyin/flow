package com.apabi.flow.ip;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Author pipi
 * @Date 2018/9/18 17:30
 **/
public class KuaidailiIpTest {


    public static void main(String[] args) {
        String[] addresses = {
                "114.235.23.75:9000",
                "39.135.9.109:8080",
                "113.74.172.185:33380",
                "60.191.57.79:10800",
                "115.223.254.40:9000",
                "115.223.248.58:9000",
                "115.46.76.219:8123",
                "18.130.131.111:8080",
                "180.118.92.148:9000",
                "123.134.133.200:49154",
                "183.21.81.20:57288",
                "183.129.244.15:10080",
                "121.10.1.181:8080",
                "180.118.86.197:9000",
                "223.150.39.185:9000"
        };
        BlockingQueue<String> addressQueue = new LinkedBlockingDeque<>();
        for (String address : addresses) {
            addressQueue.add(address);
        }

        Consumer consumer = new Consumer(addressQueue);
        for (int i = 0; i < 10; i++) {
            new Thread(consumer).start();
        }
    }
}
