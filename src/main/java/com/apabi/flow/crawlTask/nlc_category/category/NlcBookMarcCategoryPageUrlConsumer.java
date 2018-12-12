package com.apabi.flow.crawlTask.nlc_category.category;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc_category.util.CrawlNlcMarcCategoryUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-11-29 14:36
 **/
public class NlcBookMarcCategoryPageUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NlcBookMarcCategoryPageUrlConsumer.class);
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CloseableHttpClient client;
    private LinkedBlockingQueue<String> marcHrefQueue;
    private LinkedBlockingQueue<String> pageUrlQueue;
    private final CountDownLatch pageUrlCountDownLatch;

    public NlcBookMarcCategoryPageUrlConsumer(LinkedBlockingQueue<String> pageUrlQueue,CloseableHttpClient client ,LinkedBlockingQueue<String> marcHrefQueue, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch pageUrlCountDownLatch) {
        this.pageUrlQueue = pageUrlQueue;
        this.marcHrefQueue = marcHrefQueue;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.pageUrlCountDownLatch = pageUrlCountDownLatch;
        this.client = client;
    }

    @Override
    public void run() {
        String ip = "";
        String port = "";
        String pageUrl = "";
        CloseableHttpResponse pageResponse = null;
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            // 分类分页的url
            pageUrl = pageUrlQueue.take();
            Thread.sleep(10000);
            String pageHtml = CrawlNlcMarcCategoryUtil.crawlHtmlPageByPageUrl(client,pageUrl, nlcIpPoolUtils);
            if (StringUtils.isNotEmpty(pageHtml)) {
                Document pageDocument = Jsoup.parse(pageHtml);
                // 解析页面中的marc数据链接
                Elements marcElements = pageDocument.select("div[class='itemtitle']");
                for (Element marcElement : marcElements) {
                    String marcHref = marcElement.child(0).attr("href");
                    marcHref = marcHref.replace("format=999", "format=001");
                    marcHrefQueue.put(marcHref);
                }
                LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取连接为：" + pageUrl + "成功，还剩余" + pageUrlCountDownLatch.getCount() + "项，其中有" + marcElements.size() + "条数据...");
            }
        } catch (Exception e) {
            LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取连接为：" + pageUrl + "失败，原因为：" + e.getMessage() + "，还剩余" + pageUrlCountDownLatch.getCount() + "项");
        } finally {
            if (pageResponse != null) {
                try {
                    pageResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pageUrlCountDownLatch.countDown();
        }
    }
}
