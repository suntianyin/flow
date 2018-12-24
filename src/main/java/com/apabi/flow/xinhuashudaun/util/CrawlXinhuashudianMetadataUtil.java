package com.apabi.flow.xinhuashudaun.util;

import com.apabi.flow.xinhuashudaun.model.XinhuashudianItemUrl;
import com.apabi.flow.xinhuashudaun.model.XinhuashudianMetadata;
import org.apache.commons.lang.StringUtils;
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

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018-12-14 14:19
 **/
public class CrawlXinhuashudianMetadataUtil {
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
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据给定的url抓取指定的xinhuashudian数据
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static XinhuashudianMetadata crawlByUrl(String url, String ip, String port) throws IOException {
        XinhuashudianMetadata xinhuashudianMetadata = null;
        if (StringUtils.isNotEmpty(url)) {
            CloseableHttpClient client = getCloseableHttpClient(ip, port);
            HttpGet httpGet = generateHttpGet(url);
            httpGet.setHeader("Cookie", "UM_distinctid=167ab682725c5-075e97e9a4dd29-6a11177a-1fa400-167ab682726c8; taid=3f02898d-0701-4eae-8112-fdd9c93bf8f2; acw_tc=7b39758315447690559248380e52a615fd97d8a56eb0f96b97e9ca3127e02b; aliyungf_tc=AQAAAEIiuUD1cgAA4SRsO6OlZJUj0mGI; msid=GBubhuJacDP1OGIJ6zKNPcHgeBkkm6TT; CNZZDATA1274306588=1712833179-1544765219-https%253A%252F%252Fsearch.xhsd.com%252F%7C1545028782; addressInfo=%7B%22provinceId%22%3A110000%2C%22cityId%22%3A110100%2C%22regionId%22%3A110101%7D");
            httpGet.setHeader("Host", "item.xhsd.com");
            CloseableHttpResponse response = client.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            xinhuashudianMetadata = parse(html);
            xinhuashudianMetadata.setItemId(url.substring(url.lastIndexOf("/") + 1, url.length()));
        }
        return xinhuashudianMetadata;
    }

    /**
     * 根据给定的html解析出XinhuashudianMetadata
     *
     * @param html
     * @return
     */
    private static XinhuashudianMetadata parse(String html) {
        XinhuashudianMetadata xinhuashudianMetadata = null;
        if (html != null) {
            xinhuashudianMetadata = new XinhuashudianMetadata();
            Document document = Jsoup.parse(html);
            if (document != null) {
                Elements parameterElements = document.select("td[class='main-parameter']");
                for (Element parameterElement : parameterElements) {
                    String name = parameterElement.text();
                    if ("商品编码（isbn）".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setIsbn(value);
                    }
                    if ("出版时间".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setIssuedDate(value);
                    }
                    if ("出版社".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setPublisher(value);
                    }
                    if ("作者".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setAuthor(value);
                    }
                    if ("页数".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setPages(value);
                    }
                    if ("CIP核字".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setCip(value);
                    }
                    if ("正文语种".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setLanguage(value);
                    }
                    if ("开本".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setFormat(value);
                    }
                    if ("印刷时间".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setPrintTime(value);
                    }
                    if ("包装".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setBinding(value);
                    }
                    if ("出次".equals(name)) {
                        Element valueElement = parameterElement.siblingElements().get(0);
                        String value = valueElement.text();
                        xinhuashudianMetadata.setEditionOrder(value);
                    }
                }
                String title = document.select("h1[id='js-item-name']").get(0).text();
                xinhuashudianMetadata.setTitle(title);
                xinhuashudianMetadata.setCreateTime(new Date());
                xinhuashudianMetadata.setUpdateTime(new Date());
                String imgUrl = document.select("img[class='js-main-image']").get(0).attr("src");
                if (imgUrl != null && !imgUrl.startsWith("http:")) {
                    imgUrl = "http:" + imgUrl;
                }
                xinhuashudianMetadata.setCoverImgUrl(imgUrl);
                xinhuashudianMetadata.setMetaId(null);
            }
        }
        return xinhuashudianMetadata;
    }


    /**
     * 根据给定的url抓取页面上的itemUrl
     *
     * @param url
     * @param ip
     * @param port
     * @return
     * @throws IOException
     */
    public static List<XinhuashudianItemUrl> crawlXinhuashudianItemUrlListByUrl(String url, String ip, String port) throws IOException {
        List<XinhuashudianItemUrl> xinhuashudianItemUrlList = new ArrayList<XinhuashudianItemUrl>();
        HttpGet httpGet = generateHttpGet(url);
        httpGet.setHeader("Cookie", "acw_tc=7b39758815447689812227540e7d5c14f5fc3175d27582cc6418c65b2de53b; UM_distinctid=167ab682725c5-075e97e9a4dd29-6a11177a-1fa400-167ab682726c8; taid=3f02898d-0701-4eae-8112-fdd9c93bf8f2; addressInfo=%7B%22provinceId%22%3A110000%2C%22cityId%22%3A110100%2C%22regionId%22%3A110101%7D; keyword_history=%5B%7B%22name%22%3A%229787539054674%22%7D%5D; aliyungf_tc=AQAAAHFUC29RLgYA4SRsO8TZE9wX1htq; msid=Gmf2tkR_dYz-iY9nbATYIMGosI7VN6lz; CNZZDATA1274306588=711192436-1544765219-%7C1545127420");
        httpGet.setHeader("Host", "search.xhsd.com");
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        CloseableHttpResponse response = client.execute(httpGet);
        String html = EntityUtils.toString(response.getEntity());
        Document document = Jsoup.parse(html);
        Elements elements = document.select("p[class='product-desc']");
        for (Element element : elements) {
            XinhuashudianItemUrl xinhuashudianItemUrl = new XinhuashudianItemUrl();
            String href = element.child(0).attr("href");
            if (!href.startsWith("http")) {
                href = "https:" + href;
            }
            xinhuashudianItemUrl.setStatus("0");
            xinhuashudianItemUrl.setUrl(href);
            xinhuashudianItemUrlList.add(xinhuashudianItemUrl);
        }
        return xinhuashudianItemUrlList;
    }

    public static void main(String[] args) throws IOException {
        XinhuashudianMetadata xinhuashudianMetadata = crawlByUrl("https://item.xhsd.com/items/1010000102839718", "193.150.107.150", "38521");
        System.out.println(xinhuashudianMetadata);
    }
}
