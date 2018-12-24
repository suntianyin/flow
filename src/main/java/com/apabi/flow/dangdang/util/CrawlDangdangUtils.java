package com.apabi.flow.dangdang.util;

import com.apabi.flow.dangdang.model.DangdangMetadata;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
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

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-7 11:27
 **/
public class CrawlDangdangUtils {

    /**
     * 切ip重试次数
     */
    private static final int RETRY_COUNT = 3;

    /**
     * 根据指定的ip和port获取HttpClient对象
     *
     * @param ip   代理ip
     * @param port 代理ip的port
     * @return
     */
    public static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
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
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(60000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(requestRetryHandler).setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    /**
     * 根据给定的url创建HttpGet对象
     *
     * @param url
     * @return
     */
    public static HttpGet generateHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "__permanent_id=20181113092406909899805389291177606; __ddc_15d=1544075501%7C!%7C_ddclickunion%3D419-858608%257C00L51c43f9ec5df67890; __ddc_15d_f=1544075501%7C!%7C_ddclickunion%3D419-858608%257C00L51c43f9ec5df67890; pos_9_end=1544165114170; ad_ids=2487868%2C2877331%2C2817183%2C2810610%2C2642634%2C2642583%2C2642502%2C2642412%2C1930266%2C2830011%2C2674940%2C2792194%2C2792174%2C2842792%2C2757240%2C2116603%2C2841771%7C%233%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C3%2C1%2C1; dest_area=country_id%3D9000%26province_id%3D111%26city_id%3D1%26district_id%3D1110101%26town_id%3D-1; nTalk_CACHE_DATA={uid:dd_1000_ISME9754_guest4B8ECB0D-4380-34,tid:1544076366961189}; NTKF_T2D_CLIENTID=guest4B8ECB0D-4380-3486-B473-0AAB27ED13FA; __rpm=s_605253.451680112839%2C451680112840.28.1544165637350%7Cp_25303492...1544165659043; producthistoryid=25269674%2C25303492%2C25309682%2C25294617%2C25233197%2C22880871; ddscreen=2; __out_refer=; __trace_id=20181207173238030787370961801207644; __visit_id=20181207173238028764123651555823397; pos_6_end=1544175159901; pos_6_start=1544175178735");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据给定的url抓取当当书籍链接
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static List<String> crawlDangdangMetaUrlByPage(String url, String ip, String port) throws IOException {
        List<String> dangdangMetadataList = new ArrayList<>();
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        HttpGet httpGet = generateHttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String html = EntityUtils.toString(response.getEntity());
        Document document = Jsoup.parse(html);
        Elements elements = document.select("a[dd_name='单品图片']");
        for (Element element : elements) {
            String href = element.attr("href");
            dangdangMetadataList.add(href);
        }
        return dangdangMetadataList;
    }


    /**
     * 根据给定的url抓取dangdang数据
     *
     * @param url
     * @param ip
     * @param port
     * @return
     * @throws IOException
     */
    public static DangdangMetadata crawlDangdangMetaByUrl(String url, String ip, String port) throws IOException {
        DangdangMetadata dangdangMetadata = null;
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        HttpGet httpGet = generateHttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String html = EntityUtils.toString(response.getEntity());
        if (html != null) {
            dangdangMetadata = new DangdangMetadata();
            Document document = Jsoup.parse(html);
            String pid = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            dangdangMetadata.setPid(pid);
            String title = document.select("div[class='name_info']").get(0).text();
            String isbn13 = "";
            String isbn10 = "";
            String classification = "";
            Elements infoElements = document.select("ul[class='key clearfix']").get(0).children();
            for (Element infoElement : infoElements) {
                if (infoElement.text().contains("ISBN")) {
                    if (infoElement.text().split("：")[1].length() == 10) {
                        isbn10 = infoElement.text().split("：")[1];
                    } else if (infoElement.text().split("：")[1].length() == 13) {
                        isbn13 = infoElement.text().split("：")[1];
                    }
                }

                if (infoElement.text().contains("所属分类")) {
                    Elements elements = infoElement.select("span[class='lie']");
                    for (Element element : elements) {
                        classification += element.text() + ";";
                    }
                }
            }
            if (classification.endsWith(";")) {
                classification = classification.substring(0, classification.length() - 1);
            }
            dangdangMetadata.setIsbn10(isbn10);
            dangdangMetadata.setIsbn13(isbn13);
            dangdangMetadata.setClassification(classification);
            dangdangMetadata.setTitle(title);
            dangdangMetadata.setCreateTime(new Date());
            dangdangMetadata.setUpdateTime(new Date());
        }
        return dangdangMetadata;
    }

    public static String crawlDangdangPriceUrlPage(String url, String ip, String port) throws IOException {
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        HttpGet httpGet = generateHttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            Elements elements = document.select("a[name='bottom-page-turn']");
            int index = elements.size() - 1;
            Element element = elements.get(index);
            String pageNum = element.text();
            System.out.println("页码数为：" + pageNum);
            return pageNum;
        }
        return "0";
    }

    public static void main(String[] args) throws IOException {
        crawlDangdangPriceUrlPage("http://category.dangdang.com/cp01.03.00.00.00.00-srsort_pubdate_desc-f0%7C0%7C0%7C0%7C0%7C1%7C0%7C0%7C0%7C0%7C0%7C0%7C0%7C0%7C-lp22-hp23.html", "176.196.238.234", "44648");
    }
}
