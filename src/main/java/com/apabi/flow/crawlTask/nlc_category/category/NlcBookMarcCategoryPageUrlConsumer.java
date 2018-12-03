package com.apabi.flow.crawlTask.nlc_category.category;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc_category.util.CrawlNlcMarcCategoryUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018-11-29 14:36
 **/
public class NlcBookMarcCategoryPageUrlConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NlcBookMarcCategoryPageUrlConsumer.class);
    private static final Random RANDOM = new Random();
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CloseableHttpClient client;
    private List<String> marcHrefList;
    private LinkedBlockingQueue<String> pageUrlQueue;
    private CountDownLatch pageUrlCountDownLatch;
    private String categoryCode;

    public NlcBookMarcCategoryPageUrlConsumer(LinkedBlockingQueue<String> pageUrlQueue, List<String> marcHrefList, String categoryCode, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch pageUrlCountDownLatch, CloseableHttpClient client) {
        this.pageUrlQueue = pageUrlQueue;
        this.marcHrefList = marcHrefList;
        this.categoryCode = categoryCode;
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
            HttpGet pageHttpGet = CrawlNlcMarcCategoryUtil.generateHttpGet(pageUrl);
            // 每执行一次切换一次ip
            pageHttpGet = CrawlNlcMarcCategoryUtil.switchIp(pageHttpGet, nlcIpPoolUtils, ip, port);
            // 防封ip
            Thread.sleep(RANDOM.nextInt(300) + 200);
            pageResponse = client.execute(pageHttpGet);
            String pageHtml = EntityUtils.toString(pageResponse.getEntity(), "UTF-8");
            if (StringUtils.isNotEmpty(pageHtml)) {
                Document pageDocument = Jsoup.parse(pageHtml);
                // 解析页面中的marc数据链接
                Elements marcElements = pageDocument.select("div[class='itemtitle']");
                //LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "获取分类码为" + categoryCode + "连接为：" + pageUrl + "，还剩余" + pageUrlCountDownLatch.getCount() + "项，其中有" + marcElements.size() + "条数据...");
                LOGGER.info( pageUrl + "执行成功！！！即将执行减去1...");
                for (Element marcElement : marcElements) {
                    String marcHref = marcElement.child(0).attr("href");
                    marcHref = marcHref.replace("format=999", "format=001");
                    marcHrefList.add(marcHref);
                }
            }
        } catch (Exception e) {
            LOGGER.info( pageUrl + "执行失败，原因为："+e.getMessage()+"即将执行减去1...");
            //e.printStackTrace();
            //LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取分类码为" + categoryCode + "连接为：" + pageUrl + "的页面数据失败，还剩余" + pageUrlCountDownLatch.getCount() + "项");
        } finally {
            if (pageResponse != null) {
                try {
                    pageResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pageUrlCountDownLatch.countDown();
            LOGGER.info( pageUrl + "finally--------------->执行减去1...结果为："+pageUrlCountDownLatch.getCount());
        }
    }
}
