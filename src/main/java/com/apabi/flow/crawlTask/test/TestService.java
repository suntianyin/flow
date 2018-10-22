package com.apabi.flow.crawlTask.test;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author pipi
 * @Date 2018/10/22 9:19
 **/
//@Component
public class TestService implements ApplicationRunner {
    @Scheduled(cron = "0/10 * *  * * ? ")
    public void runTest() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(Thread.currentThread().getName() + "正在执行test....");
    }
}
