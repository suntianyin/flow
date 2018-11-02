package com.apabi.flow.newspaper.util;

import com.apabi.flow.crawlTask.util.UserAgentUtils;
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
        httpGet.setHeader("Host", "news.cnr.cn");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", UserAgentUtils.getUserAgent());
        return httpGet;
    }

    // 获取页数
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
    public static List<Newspaper> crawlByUrl(CnrIpPoolUtils cnrIpPoolUtils, String url, CloseableHttpClient httpClient){
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
                HttpGet httpGet1 = generateHttpGet(href);
                // 获取ip
                String host1 = cnrIpPoolUtils.getIp();
                // 创建RequestConfig对象，切换ip
                HttpHost httpHost1 = new HttpHost(host1.split(":")[0], Integer.parseInt(host1.split(":")[1]));
                RequestConfig requestConfig1 = RequestConfig.custom().setProxy(httpHost1).build();
                httpGet.setConfig(requestConfig1);
                CloseableHttpResponse response1 = httpClient.execute(httpGet1);
                String htmlContent = EntityUtils.toString(response1.getEntity(), "GBK");
                newspaper.setHtmlContent(htmlContent);
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

}
