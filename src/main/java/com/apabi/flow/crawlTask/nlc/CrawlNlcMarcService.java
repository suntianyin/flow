package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Spring boot初始化完毕后开始执行nlcMarc爬虫代码
 *
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
// 先注释掉，不执行爬虫操作
// @Order(1)
// @Component
public class CrawlNlcMarcService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(CrawlNlcMarcService.class);
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("spring boot初始化完毕，开始执行国图爬虫....");
        // 模拟一些isbn
        String[] isbnList = {
                "9787205033781",
                "9787502767341",
                "9787802304857",
                "9787536683396",
                "9787300077741",
                "9787802290167",
                "9787801645203",
                "9787810857338",
                "9787501438495",
                "9787020058792",
                "9787802212473",
                "9787534022357",
                "9787802033627",
                "9787020059331",
                "9787508443232",
                "9787508243801",
                "9787801107718",
                "9787301113714",
                "9787112088546",
                "9787115160119",
                "9787560939698",
                "9787533736620",
                "9787500594659",
                "9787811102819",
                "9787810815871",
                "9787122000316",
                "9787106026349",
                "9787810607254",
                "9787544502696",
                "9787500117032",
                "9787807163817",
                "9787509201466",
                "9787502767167",
                "9787502767150",
                "9787538148763",
                "9787900722041",
                "9787538721935",
                "9787102038292",
                "9787219058046",
                "9787115137388",
                "9787532741434",
                "9787503666667",
                "9787503846038",
                "9787040153590",
                "9787040189971",
                "9787530208816",
                "9787040210682",
                "9787501957453",
                "9787121034954",
                "9787802267701",
                "9787531817642",
                "9787802267527",
                "9787111208921",
                "9787111207344",
                "9787116052604",
                "9787540738983",
                "9787806819838",
                "9787534379321",
                "9787538847857",
                "9787806577189",
                "9787802212541",
                "9787801165879",
                "9787503670879",
                "9787530534168",
                "9787540738341",
                "9787208068070",
                "9787111062622",
                "9787533146092",
                "9787810886451",
                "9787117083799",
                "9787501953288",
                "9787807020653",
                "9787532250974",
                "9787501177721",
                "9787301113738",
                "9787301116487",
                "9787111210689",
                "9787802269330",
                "9787802269453",
                "9787531542452",
                "9787810895484",
                "9787030185785"
        };
        // 定义队列大小
        int queueSize = 100;
        // 将isbn添加到队列中
        ArrayBlockingQueue<String> isbnQueue = new ArrayBlockingQueue<>(queueSize);
        // 创建生产者对象
        NlcMarcProducer producer = new NlcMarcProducer(isbnQueue, "nlcMarcProducer", isbnList);
        // 开启生产者线程
        Thread producerThread = new Thread(producer);
        producerThread.start();
        // 获取cpu的核数
        int cpuProcessorAmount = Runtime.getRuntime().availableProcessors();
        // 设置线程数
        int threadAmount = 5 * cpuProcessorAmount;
        // 创建消费者对象
        NlcMarcConsumer nlcMarcConsumer = new NlcMarcConsumer(isbnQueue, nlcBookMarcDao);
        // 创建线程池对象
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
        for (int i = 0; i < isbnList.length; i++) {
            // 执行线程中的任务
            executorService.execute(nlcMarcConsumer);
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
