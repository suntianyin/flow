package com.apabi.flow.crawlTask.nlc_category.page;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc_category.dao.NlcBookMarcCategoryDao;
import com.apabi.flow.nlcmarc_category.model.NlcBookMarcCategory;
import com.apabi.flow.nlcmarc_category.util.CrawlNlcMarcCategoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/21 14:28
 **/
public class NlcMarcCategoryPageConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NlcMarcCategoryPageConsumer.class);
    private LinkedBlockingQueue<String> idQueue;
    private CountDownLatch countDownLatch;
    private NlcBookMarcCategoryDao nlcBookMarcCategoryDao;
    private NlcIpPoolUtils ipPoolUtils;

    public NlcMarcCategoryPageConsumer(LinkedBlockingQueue idQueue, CountDownLatch countDownLatch, NlcBookMarcCategoryDao nlcBookMarcCategoryDao, NlcIpPoolUtils ipPoolUtils) {
        this.idQueue = idQueue;
        this.countDownLatch = countDownLatch;
        this.nlcBookMarcCategoryDao = nlcBookMarcCategoryDao;
        this.ipPoolUtils = ipPoolUtils;
    }


    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        try {
            id = idQueue.take();
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            Integer pageNumber = CrawlNlcMarcCategoryUtil.getCategoryPageNumber(id, ip, port);
            NlcBookMarcCategory nlcBookMarcCategory = nlcBookMarcCategoryDao.findById(id);
            nlcBookMarcCategory.setPage(pageNumber);
            nlcBookMarcCategoryDao.update(nlcBookMarcCategory);
        } catch (Exception e) {
            LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "获取" + id + "的页码失败...");
        } finally {
            countDownLatch.countDown();
        }
    }
}
