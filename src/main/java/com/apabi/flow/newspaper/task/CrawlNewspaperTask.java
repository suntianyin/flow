package com.apabi.flow.newspaper.task;

import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.model.Newspaper;
import com.apabi.flow.newspaper.util.CnrCrawlUtils;
import com.apabi.flow.newspaper.util.CnrIpPoolUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/11/2 11:10
 **/
public class CrawlNewspaperTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlNewspaperTask.class);
    private NewspaperDao newspaperDao;
    private CountDownLatch countDownLatch;
    private CnrIpPoolUtils cnrIpPoolUtils;
    private CloseableHttpClient closeableHttpClient;
    private String url;

    public CrawlNewspaperTask(NewspaperDao newspaperDao, CountDownLatch countDownLatch, String url, CnrIpPoolUtils cnrIpPoolUtils, CloseableHttpClient closeableHttpClient) {
        this.countDownLatch = countDownLatch;
        this.url = url;
        this.cnrIpPoolUtils = cnrIpPoolUtils;
        this.closeableHttpClient = closeableHttpClient;
        this.newspaperDao = newspaperDao;
    }

    @Override
    public void run() {
        try {
            List<Newspaper> newspaperResultList = CnrCrawlUtils.crawlByUrl(cnrIpPoolUtils, url, closeableHttpClient);
            System.out.println("抓取的新闻列表大小为："+newspaperResultList.size());
            for (Newspaper newspaper : newspaperResultList) {
                newspaperDao.insert(newspaper);
                LOGGER.info("抓取"+newspaper.getTitle()+"并插入数据库成功");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }

    }
}
