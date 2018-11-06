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

/**
 * @Author pipi
 * @Date 2018/11/5 9:41
 **/
public class ChinanewsCrawlUtils {
    // 重试次数
    private static final int RETRY_COUNT = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinanewsCrawlUtils.class);

    // 生成HttpClient
    public static CloseableHttpClient getCloseableHttpClient() {
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
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(requestRetryHandler).setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    // 生成请求
    private static HttpGet generateHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "channel.chinanews.com");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        return httpGet;
    }

    public static void crawlByUrl(CnrIpPoolUtils cnrIpPoolUtils, String url, CloseableHttpClient httpClient, NewspaperDao newspaperDao) {
        // 访问专题页面
        HttpGet httpGet = generateHttpGet(url);
        // 设置代理ip
        String host = cnrIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        httpGet.setConfig(requestConfig);
        // 设置请求头
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
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
                    HttpGet httpGet1 = generateHttpGet(url1.toString());
                    httpGet1.setHeader("Host", "www.chinanews.com");
                    String host1 = cnrIpPoolUtils.getIp();
                    String ip1 = host1.split(":")[0];
                    String port1 = host1.split(":")[1];
                    HttpHost httpHost1 = new HttpHost(ip1, Integer.parseInt(port1));
                    RequestConfig requestConfig1 = RequestConfig.custom().setProxy(httpHost1).build();
                    httpGet1.setConfig(requestConfig1);
                    httpGet1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
                    // 抓取详情页
                    CloseableHttpResponse response1 = null;
                    try {
                        response1 = httpClient.execute(httpGet1);
                        if (response1.getStatusLine().getStatusCode() == 200) {
                            String s = EntityUtils.toString(response1.getEntity(), "GBK");
                            newspaper.setHtmlContent(s);
                            try {
                                newspaperDao.insert(newspaper);
                                LOGGER.info(Thread.currentThread().getName() + "使用" + ip1 + ":" + port1 + "抓取" + newspaper.getTitle() + "并插入数据库成功");
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
