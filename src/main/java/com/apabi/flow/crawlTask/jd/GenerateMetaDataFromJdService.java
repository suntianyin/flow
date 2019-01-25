package com.apabi.flow.crawlTask.jd;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.jd.model.JdMetadata;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.model.SystemConf;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-14 14:27
 **/
//@Order(6)
//@Component
public class GenerateMetaDataFromJdService implements ApplicationRunner {
    @Autowired
    private JdMetadataDao jdMetadataDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private SystemConfMapper systemConfMapper;

    /**
     * 每小时执行一次根据京东生成元数据
     */
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void runTask() {
        run(null);
    }

    @Override
    public void run(ApplicationArguments args) {
        SystemConf threadPoolConf = systemConfMapper.selectByConfKey("create_meta_from_jd_data_thread_pool_size");
        SystemConf listSizeConf = systemConfMapper.selectByConfKey("create_meta_from_jd_data_list_size");
        int create_meta_from_jd_data_thread_pool_size = Integer.parseInt(threadPoolConf.getConfValue());
        int create_meta_from_jd_data_list_size = Integer.parseInt(listSizeConf.getConfValue());
        CloseableHttpClient httpClient = getCloseableHttpClient();
        PageHelper.startPage(1, create_meta_from_jd_data_list_size);
        Page<JdMetadata> jdMetadataList = jdMetadataDao.findShouldCrawl();
        LinkedBlockingQueue<JdMetadata> jdMetadataQueue = new LinkedBlockingQueue<>(jdMetadataList);
        ExecutorService executorService = Executors.newFixedThreadPool(create_meta_from_jd_data_thread_pool_size);
        int listSize = jdMetadataList.size();
        CountDownLatch countDownLatch = new CountDownLatch(listSize);
        GenerateMetaDataFromJdConsumer consumer = new GenerateMetaDataFromJdConsumer(jdMetadataQueue, jdMetadataDao, countDownLatch, httpClient);
        for (int i = 0; i < listSize; i++) {
            executorService.execute(consumer);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    /**
     * 创建 CloseableHttpClient 对象
     *
     * @return
     */
    private CloseableHttpClient getCloseableHttpClient() {
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(60000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }
}