package com.apabi.flow.newspaper.cnr.util;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/11/1 11:16
 **/
public class CnrCrawlUtils {
    // 重试次数
    private static final int RETRY_COUNT = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(CnrCrawlUtils.class);

    /**
     * 生成HttpClient
     *
     * @return
     */
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

    /**
     * 生成请求
     *
     * @param url 请求的url路径
     * @return
     */
    private static HttpGet generateHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        // httpGet.setHeader("Host", "www.cnr.cn");
        // httpGet.setHeader("Referer","http://sports.cnr.cn/synthesize/news/");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("Cookie", "wdcid=774faac7ea5e7be1; wdses=2f8df3aa34f1d386; wdlast=1542014903");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 获取央广网的页码
     *
     * @param url
     * @return
     */
    public static int getPageNum(String url) {
        int pageNum = 0;
        HttpGet httpGet = generateHttpGet(url);
        CloseableHttpClient httpClient = getCloseableHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String homeHtml = EntityUtils.toString(response.getEntity());
            // 获取页数
            pageNum = Integer.parseInt(homeHtml.split("<script>createPageHTML\\(")[1].split(",")[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageNum;
    }

    // 根据url中的新闻详情进行抓取
    public static List<Newspaper> crawlByUrl(CnrIpPoolUtils cnrIpPoolUtils, String url, CloseableHttpClient httpClient) {
        List<Newspaper> newspaperList = new ArrayList<>();
        HttpGet httpGet = generateHttpGet(url);
        // 获取ip
        String host = cnrIpPoolUtils.getIp();
        // 创建RequestConfig对象，切换ip
        HttpHost httpHost = new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity(), "GBK");
            Document document = Jsoup.parse(html);
            Elements newspaperElements = document.select("h3[class='f18 lh24 left fb yahei']");
            for (Element newspaperElement : newspaperElements) {
                Newspaper newspaper = new Newspaper();
                String title = newspaperElement.text();
                String href = "http://news.cnr.cn/native/" + newspaperElement.child(0).attr("href").substring(2, newspaperElement.child(0).attr("href").length());
                String abstract_ = newspaperElement.nextElementSibling().nextElementSibling().nextElementSibling().select("p[class='left f14 lh24 yahei']").text();
                newspaper.setTitle(title);
                newspaper.setAbstract_(abstract_);
                newspaper.setUrl(href);
                newspaperList.add(newspaper);
                //LOGGER.info("使用" + host1.split(":")[0] + ":" + host1.split(":")[1] + "抓取---->" + title + "<----成功...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newspaperList;
    }

    /**
     * 根据url抓取每条新闻的标题、摘要、地址
     *
     * @param cnrIpPoolUtils kuaidaili的ip生成策略
     * @param url            专栏中分页的地址
     * @param httpClient
     * @return
     */
    public static List<Newspaper> crawlByUrlEduAndSport(CnrIpPoolUtils cnrIpPoolUtils, String url, CloseableHttpClient httpClient) {
        List<Newspaper> newspaperList = new ArrayList<>();
        HttpGet httpGet = generateHttpGet(url);
        // 获取ip
        String host = cnrIpPoolUtils.getIp();
        // 创建RequestConfig对象，切换ip
        HttpHost httpHost = new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity(), "GBK");
            Document document = Jsoup.parse(html);
            Elements newspaperElements = document.select("div[class='text']");
            for (Element newspaperElement : newspaperElements) {
                Newspaper newspaper = new Newspaper();
                String title = newspaperElement.child(0).child(0).text();
                String href = newspaperElement.child(0).child(0).attr("href");
                String abstract_ = newspaperElement.child(1).text();
                newspaper.setTitle(title);
                newspaper.setAbstract_(abstract_);
                newspaper.setUrl(href);
                newspaper.setSource("中央人民广播电台网");
                newspaperList.add(newspaper);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newspaperList;
    }

    public static void main(String[] args) {
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        CloseableHttpClient closeableHttpClient = getCloseableHttpClient();
        crawlByUrlEduAndSport(cnrIpPoolUtils, "http://sports.cnr.cn/synthesize/news/index_1.html", closeableHttpClient);
    }
}
