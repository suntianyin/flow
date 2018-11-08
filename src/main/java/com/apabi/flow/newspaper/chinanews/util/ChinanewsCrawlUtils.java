package com.apabi.flow.newspaper.chinanews.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.model.Newspaper;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/11/5 16:02
 **/
public class ChinanewsCrawlUtils {
    // 抓取成功状态码
    private static final int CRAWL_SUCCESS_CODE = 200;
    // 请求重试切换IP次数
    private static final int SWITCH_IP_COUNT = 3;
    // 请求重试次数
    private static final int RETRY_COUNT = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinanewsCrawlUtils.class);

    /**
     * 生成HttpClient对象
     *
     * @param ip
     * @param port
     * @return
     */
    public static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        HttpRequestRetryHandler requestRetryHandler = (exception, executionCount, context) -> {
            if (executionCount >= RETRY_COUNT) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                // Connection refused
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }


            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        };
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setSocketTimeout(30000).setProxy(httpHost).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(requestRetryHandler).setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    /**
     * 生成请求
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
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "cnsuuid=794d06aa-0935-e611-aac7-3290d191bf369669.258034995362_1541131500267; CNZZDATA1263394109=1073150873-1541126433-http%253A%252F%252Fwww.chinanews.com%252F%7C1541573559; Hm_lvt_0da10fbf73cda14a786cd75b91f6beab=1541131501,1541381725,1541574628; Hm_lpvt_0da10fbf73cda14a786cd75b91f6beab=1541574628; cn_1263394109_dplus=%7B%22distinct_id%22%3A%20%22166d299a95a658-08d85c5472284f-414a0029-1fa400-166d299a95b222%22%2C%22sp%22%3A%20%7B%22%24_sessionid%22%3A%200%2C%22%24_sessionTime%22%3A%201541574873%2C%22%24dp%22%3A%200%2C%22%24_sessionPVTime%22%3A%201541574873%7D%7D; UM_distinctid=166d299a95a658-08d85c5472284f-414a0029-1fa400-166d299a95b222");
        httpGet.setHeader("Host", "www.chinanews.com");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据url抓取每个新闻页面中每条新闻数据
     *
     * @param url
     * @param cnrIpPoolUtils
     * @param newspaperDao
     */
    public static void crawlByUrl(String url, CnrIpPoolUtils cnrIpPoolUtils, NewspaperDao newspaperDao) {
        // 访问专题页面
        HttpGet httpGet = generateHttpGet(url);
        // 设置请求头
        CloseableHttpResponse response = null;
        String host = cnrIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
        try {
            response = httpClient.execute(httpGet);
            int count = 0;
            while (response.getStatusLine().getStatusCode() != CRAWL_SUCCESS_CODE) {
                if (count == SWITCH_IP_COUNT) {
                    LOGGER.error("Chinanews失败的页面：" + url);
                    break;
                }
                host = cnrIpPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                httpClient = getCloseableHttpClient(ip, port);
                response = httpClient.execute(httpGet);
                count++;
            }
            String html = EntityUtils.toString(response.getEntity(), "GBK");
            html = html.substring(html.indexOf("{"), html.lastIndexOf(";"));
            JSONObject parse = JSONObject.parseObject(html);
            Object o = parse.get("docs");
            JSONArray objects = JSONObject.parseArray(o.toString());
            for (int i = 0; i < objects.size(); i++) {
                Newspaper newspaper = new Newspaper();
                Object title = JSONObject.parseObject(objects.get(i).toString()).get("title");
                Object content = JSONObject.parseObject(objects.get(i).toString()).get("content");
                Object url1 = JSONObject.parseObject(objects.get(i).toString()).get("url");
                newspaper.setTitle(title.toString());
                newspaper.setAbstract_(content.toString());
                newspaper.setUrl(url1.toString());
                try {
                    newspaperDao.insert(newspaper);
                    LOGGER.info(Thread.currentThread().getName() + "抓取" + newspaper.getTitle() + "并插入数据库成功");
                } catch (Exception e) {
                }
            }
            count++;
            LOGGER.info(Thread.currentThread().getName() + "抓取" + url + "并插入数据库成功");


            /*if (response.getStatusLine().getStatusCode() == 200) {
                String html = EntityUtils.toString(response.getEntity(), "GBK");
                html = html.substring(html.indexOf("{"), html.lastIndexOf(";"));
                JSONObject parse = JSONObject.parseObject(html);
                Object o = parse.get("docs");
                JSONArray objects = JSONObject.parseArray(o.toString());
                for (int i = 0; i < objects.size(); i++) {
                    Newspaper newspaper = new Newspaper();
                    Object title = JSONObject.parseObject(objects.get(i).toString()).get("title");
                    Object content = JSONObject.parseObject(objects.get(i).toString()).get("content");
                    Object url1 = JSONObject.parseObject(objects.get(i).toString()).get("url");
                    newspaper.setTitle(title.toString());
                    newspaper.setAbstract_(content.toString());
                    newspaper.setUrl(url1.toString());
                    try {
                        newspaperDao.insert(newspaper);
                        LOGGER.info(Thread.currentThread().getName() + "抓取" + newspaper.getTitle() + "并插入数据库成功");
                    } catch (Exception e) {
                    }
                }
                LOGGER.info(Thread.currentThread().getName() + "抓取" + url + "并插入数据库成功");
            } else {
                LOGGER.error("Chinanews失败的页面：" + url);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据url抓取新闻详情页
     *
     * @param url
     * @param cnrIpPoolUtils
     * @param newspaperDao
     */
    public static void crawlHtmlContentByUrl(String url, CnrIpPoolUtils cnrIpPoolUtils, NewspaperDao newspaperDao) {
        CloseableHttpResponse response = null;
        try {
            String host = cnrIpPoolUtils.getIp();
            String ip = host.split(":")[0];
            String port = host.split(":")[1];
            CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
            HttpGet httpGet = generateHttpGet(url);
            response = httpClient.execute(httpGet);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String format = simpleDateFormat.format(date);
            if (response.getStatusLine().getStatusCode() == 200) {
                String html = EntityUtils.toString(response.getEntity(),"GBK");
                Newspaper newspaper = newspaperDao.findByUrl(url);
                newspaper.setHtmlContent(html);
                newspaperDao.update(newspaper);
                LOGGER.info(format + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + newspaper.getTitle() + "添加至数据库成功...");
            } else {
                LOGGER.info(format + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + url + "添加至数据库失败...");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        crawlHtmlContentByUrl("http://www.chinanews.com/gn/2018/06-13/8536918.shtml", cnrIpPoolUtils, null);
    }
}
