package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
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
//@Order(1)
//@Component
public class CrawlNlcMarcService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(CrawlNlcMarcService.class);
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;
    private IpPoolUtils ipPoolUtils;

    @Override
    public void run(ApplicationArguments args) {
        ipPoolUtils = new IpPoolUtils();
        logger.info("spring boot初始化完毕，开始执行国图爬虫....");
        // 模拟一些isbn
        String[] isbnList = {
                "9787811130720",
                "9787543640863",
                "9787301111994",
                "9787508041209",
                "9787508041209",
                "9787508041247",
                "9787802078659",
                "9787802078581",
                "9787802078284",
                "9787811102567",
                "9787507827347",
                "9787811102840",
                "9787200066630",
                "9787802078680",
                "9787802078642",
                "9787802078161",
                "9787802078635",
                "9787561134290",
                "9787561123881",
                "9787561123652",
                "9787306028310",
                "9787534549519",
                "9787306028488",
                "9787810797160",
                "9787810679756",
                "9787810798051",
                "9787306028396",
                "9787306025005",
                "9787542917461",
                "9787811054408",
                "9787811055207",
                "9787208067059",
                "9787208067349",
                "9787807064190",
                "9787208067332",
                "9787501774708",
                "9787501777945",
                "9787501777730",
                "9787506236010",
                "9787801976178",
                "9787208067721",
                "9787807063483",
                "9787562018629",
                "9787562030140",
                "9787503536366",
                "9787561134306",
                "9787561121641",
                "9787561123904",
                "9787501774524",
                "9787501766857",
                "9787501771103",
                "9787536352490",
                "9787501774517",
                "9787536352544",
                "9787501774494",
                "9787501777594",
                "9787563917297",
                "9787501777303",
                "9787200067644",
                "9787562325376",
                "9787501779697",
                "9787501776443",
                "9787306025616",
                "9787301114384",
                "9787306028266",
                "9787811102819",
                "9787811102512",
                "9787224079722",
                "9787563714339",
                "9787810798266",
                "9787563714230",
                "9787219056318",
                "9787109115071",
                "9787505955912",
                "9787503536724",
                "9787560834672",
                "9787503536540",
                "9787503536564",
                "9787503536410",
                "9787503536809",
                "9787562019077",
                "9787807063940",
                "9787542916549",
                "9787534549786",
                "9787534549854",
                "9787810886741",
                "9787810886703",
                "9787807303169",
                "9787807303121",
                "9787544409735",
                "9787807303442",
                "9787544410120",
                "9787560244501",
                "9787508346526",
                "9787307054141",
                "9787307054387",
                "9787307053724",
                "9787307054158",
                "9787307054059",
                "9787109114593"
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
        NlcMarcConsumer nlcMarcConsumer = new NlcMarcConsumer(isbnQueue, nlcBookMarcDao,ipPoolUtils);
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
