package com.apabi.flow.crawlTask.nlc_category.category;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/11/28 9:49
 **/
public class NlcBookMarcCategoryConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NlcBookMarcCategoryConsumer.class);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private NlcBookMarcDao nlcBookMarcDao;
    private String marcHref;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private CountDownLatch countDownLatch;
    private CloseableHttpClient client;

    public NlcBookMarcCategoryConsumer(String marcHref, NlcIpPoolUtils nlcIpPoolUtils, CountDownLatch countDownLatch, CloseableHttpClient client, NlcBookMarcDao NlcBookMarcDao) {
        this.marcHref = marcHref;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.countDownLatch = countDownLatch;
        this.client = client;
        this.nlcBookMarcDao = NlcBookMarcDao;
    }

    @Override
    public void run() {
        try {
            String content = parse(client, marcHref, nlcIpPoolUtils);
            String host = content.split("&host&")[0];
            String marcContent = content.split("&host&")[1];
            String ip = host.split(":")[0];
            String port = host.split(":")[1];
            if (StringUtils.isNotEmpty(marcContent)) {
                NlcBookMarc nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(marcContent);
                nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
                Date date = new Date();
                String time = TIME_FORMAT.format(date);
                LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc分类抓取" + nlcBookMarc.getIsbn() + "并添加至数据库成功，还剩余" + countDownLatch.getCount() + "项...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * 根据给定的marcHref抓取marc数据
     *
     * @param client
     * @param marcHref
     * @param nlcIpPoolUtils
     * @return
     * @throws IOException
     */
    private static String parse(CloseableHttpClient client, String marcHref, NlcIpPoolUtils nlcIpPoolUtils) throws IOException {
        Random random = new Random();
        // 设置代理ip
        String host = nlcIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

        HttpGet saveHttpGet = generateHttpGet(marcHref);
        saveHttpGet.setConfig(requestConfig);

        CloseableHttpResponse saveResponse = null;
        try {
            Thread.sleep(300 + random.nextInt(300));
            saveResponse = client.execute(saveHttpGet);
        } catch (IOException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
        }
        String saveHtml = EntityUtils.toString(saveResponse.getEntity(), "UTF-8");
        Document parse = Jsoup.parse(saveHtml);
        // 保存按钮链接
        String saveHref = parse.select("a[title='保存/邮寄']").attr("href");
        String docNumber = saveHref.split("doc_number=")[1];
        String var7 = "&option_type=&format=997&encoding=NONE&SUBJECT=&NAME=&EMAIL=&text=&x=90&y=21";
        String var6 = "?func=full-mail&doc_library=NLC01&doc_number=";
        String var5 = saveHref.split("\\?")[0];
        String downloadUrl = var5 + var6 + docNumber + var7;
        HttpGet downloadHttpGet = generateHttpGet(downloadUrl);
        downloadHttpGet.setConfig(requestConfig);

        CloseableHttpResponse downloadResponse = null;
        try {
            Thread.sleep(300 + random.nextInt(300));
            downloadResponse = client.execute(downloadHttpGet);
        } catch (IOException e) {
            throw new IOException();
        } catch (InterruptedException e) {
        }
        String downloadHtml = EntityUtils.toString(downloadResponse.getEntity(), "UTF-8");
        Document document = Jsoup.parse(downloadHtml);
        String downloadHref = document.select("p[class='text3']").get(0).child(0).attr("href");
        HttpGet marcHttpGet = generateHttpGet(downloadHref);
        marcHttpGet.setConfig(requestConfig);
        CloseableHttpResponse marcResponse = null;
        try {
            Thread.sleep(300 + random.nextInt(300));
            marcResponse = client.execute(marcHttpGet);
        } catch (IOException e) {
            throw new IOException();
        } catch (InterruptedException e) {
        }
        String content = EntityUtils.toString(marcResponse.getEntity(), "UTF-8");
        return ip + ":" + port + "&host&" + content;
    }

    /**
     * 根据给定的url创建HttpGet对象
     *
     * @param url
     * @return
     */
    private static HttpGet generateHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Cookie", "Hm_lvt_199be991c3ca69223b0d946b04112648=1542681725; Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540976739,1540977821,1542620301,1542676935");
        httpGet.setHeader("Host", "opac.nlc.cn");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

}
