package com.apabi.flow.douban.util;

import com.apabi.flow.crawlTask.util.DoubanCookieUtils;
import com.apabi.flow.crawlTask.util.UserAgentUtils;
import com.apabi.flow.douban.model.DoubanMeta;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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
 * @Date 2018/10/15 18:03
 **/
public class CrawlDoubanUtil {
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
        // 访问豆瓣主题首页
        HttpGet httpGet = new HttpGet(url);
        // 请求配置
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Request", "1");
        httpGet.setHeader("Cookie", DoubanCookieUtils.getCookie() + "; __utmt=1; __utma=30149280.97956999.1540178056.1540178056.1540178056.1; __utmb=30149280.1.10.1540178056; __utmc=30149280; __utmz=30149280.1540178056.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; as=\"https://sec.douban.com/b?r=https%3A%2F%2Fbook.douban.com%2F\"; ps=y");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 从douban根据抓取列表获取该页面中的douban数据
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static List<String> crawlDoubanIdList(String url, String ip, String port) {
        List<String> doubanMetaIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(url)) {
            // 实例化CloseableHttpClient对象
            CloseableHttpClient client = getCloseableHttpClient(ip, port);
            // 访问豆瓣主题首页
            HttpGet httpGet = generateHttpGet(url);
            CloseableHttpResponse response1 = null;
            try {
                response1 = client.execute(httpGet);
                String html = EntityUtils.toString(response1.getEntity());
                Document document = Jsoup.parse(html);
                Elements elements = document.select("a[class='nbg']");
                for (Element element : elements) {
                    String href = element.attr("href");
                    href = href.substring(0, href.lastIndexOf("/"));
                    String id = href.substring(href.lastIndexOf("/") + 1, href.length());
                    doubanMetaIdList.add(id);
                }
            } catch (IOException e) {
            } finally {
                httpGet.releaseConnection();
                httpGet.abort();
                if (response1 != null) {
                    try {
                        response1.close();
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return doubanMetaIdList;
    }

    /**
     * 根据doubanId并切换ip解析出DoubanMeta对象
     *
     * @param id
     * @param ip
     * @param port
     * @return
     */
    public static DoubanMeta crawlDoubanMetaById(String id, String ip, String port) {
        DoubanMeta doubanMeta = null;
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        String url = "https://api.douban.com/v2/book/" + id;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", UserAgentUtils.getUserAgent());
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
                doubanMeta = new DoubanMeta();
                String html = EntityUtils.toString(response.getEntity());
                doubanMeta = parseDoubanMeta(html);
            }
        } catch (IOException e) {
        } finally {
            httpGet.releaseConnection();
            httpGet.abort();
            if (response != null) {
                try {
                    response.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doubanMeta.setCreateTime(new Date());
        doubanMeta.setUpdateTime(new Date());
        return doubanMeta;
    }

    /**
     * 根据给定的isbn调用douban接口
     *
     * @param isbn
     * @param ip
     * @param port
     * @return
     */
    public static DoubanMeta crawlDoubanMetaByIsbn(String isbn, String ip, String port) throws IOException {
        DoubanMeta doubanMeta = null;
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        String url = "https://api.douban.com/v2/book/isbn/" + isbn;
        HttpGet httpGet = generateHttpGet(url);
        httpGet.setHeader("Host", "api.douban.com");
        httpGet.setHeader("Cookie", "ll=\"108169\"; bid=826yiZ-eZnk; douban-fav-remind=1; ap_v=0,6.0; gr_session_id_22c937bbd8ebd703f2d8e9445f7dfd03=4de855b1-1a0e-4d67-8fc6-afa44281cfc3; gr_session_id_22c937bbd8ebd703f2d8e9445f7dfd03_4de855b1-1a0e-4d67-8fc6-afa44281cfc3=true; viewed=\"30276249_30335756_30317420_2116333\"; gr_cs1_4de855b1-1a0e-4d67-8fc6-afa44281cfc3=user_id%3A0; gr_user_id=cc43e77d-64cd-4722-ab60-e4a0974d9e7e; _vwo_uuid_v2=DFEBCA1C98E7DDF3532A76362A6E34777|2a2ad930b768df966e67c07409a887da; __utma=30149280.1231564111.1540195466.1544671060.1544678484.8; __utmb=30149280.3.10.1544678484; __utmc=30149280; __utmz=30149280.1544671060.7.5.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                doubanMeta = new DoubanMeta();
                String html = EntityUtils.toString(response.getEntity());
                if (StringUtils.isNotEmpty(html)) {
                    doubanMeta = parseDoubanMeta(html);
                }
                doubanMeta.setCreateTime(new Date());
                doubanMeta.setUpdateTime(new Date());
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            httpGet.releaseConnection();
            httpGet.abort();
            if (response != null) {
                try {
                    response.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return doubanMeta;
    }

    /**
     * 从html中解析出DoubanMeta
     *
     * @param html
     * @return
     */
    public static DoubanMeta parseDoubanMeta(String html) {
        DoubanMeta doubanMeta = null;
        if (StringUtils.isNotEmpty(html)) {
            doubanMeta = new DoubanMeta();
            JSONObject jsonObject = JSONObject.fromObject(html);
            if (html.contains("id")) {
                doubanMeta.setDoubanId(jsonObject.getString("id"));
            }
            if (html.contains("title")) {
                doubanMeta.setTitle(jsonObject.getString("title"));
            }
            if (html.contains("author")) {
                if (jsonObject.getJSONArray("author").size() != 0) {
                    doubanMeta.setAuthor(jsonObject.getJSONArray("author").getString(0));
                }
            }
            if (html.contains("publisher")) {
                doubanMeta.setPublisher(jsonObject.getString("publisher"));
            }
            if (html.contains("alt_title")) {
                doubanMeta.setAltTitle(jsonObject.getString("alt_title"));
            }
            if (html.contains("subtitle")) {
                doubanMeta.setSubtitle(jsonObject.getString("subtitle"));
            }
            if (html.contains("translator")) {
                if (jsonObject.getJSONArray("translator").size() != 0) {
                    doubanMeta.setTranslator(jsonObject.getJSONArray("translator").getString(0));
                }
            }
            if (html.contains("isbn10")) {
                doubanMeta.setIsbn10(jsonObject.getString("isbn10"));
            }
            if (html.contains("isbn13")) {
                doubanMeta.setIsbn13(jsonObject.getString("isbn13"));
            }
            if (html.contains("pubdate")) {
                String issuedDate = jsonObject.getString("pubdate");
                if (StringUtils.isNotEmpty(issuedDate)) {
                    String s = StringToolUtil.issuedDateFormat(issuedDate);
                    if (s.contains(" 00:00:00")) {
                        issuedDate = s.replaceAll(" 00:00:00", "");
                    }
                }
                doubanMeta.setIssueddate(issuedDate);
            }
            if (html.contains("pages")) {
                doubanMeta.setPages(jsonObject.getString("pages"));
            }
            if (html.contains("price")) {
                doubanMeta.setPrice(jsonObject.getString("price"));
            }
            if (html.contains("binding")) {
                doubanMeta.setBinding(jsonObject.getString("binding"));
            }
            if (html.contains("series\":")) {
                doubanMeta.setSeries(JSONObject.fromObject(jsonObject.getString("series")).getString("title"));
            }
            if (html.contains("rating")) {
                doubanMeta.setAverage(JSONObject.fromObject(jsonObject.getString("rating")).getString("average"));
            }
            if (html.contains("summary")) {
                doubanMeta.setSummary(jsonObject.getString("summary"));
            }
            if (html.contains("author_intro")) {
                doubanMeta.setAuthorIntro(jsonObject.getString("author_intro"));
            }
            if (html.contains("catalog")) {
                doubanMeta.setCatalog(jsonObject.getString("catalog"));
            }
            if (html.contains("tags")) {
                String tags = "";
                for (Object titles : jsonObject.getJSONArray("tags")) {
                    tags += JSONObject.fromObject(titles).getString("title") + " ";
                }
                doubanMeta.setTags(tags);
            }
            if (html.contains("images")) {
                doubanMeta.setSmallCover(jsonObject.getJSONObject("images").getString("small"));
                doubanMeta.setLargeCover(jsonObject.getJSONObject("images").getString("large"));
                doubanMeta.setMediumCover(jsonObject.getJSONObject("images").getString("medium"));
            }
            if (html.contains("ebook_price")) {
                doubanMeta.setEbookPrice(jsonObject.getString("ebook_price"));
            }
            if (html.contains("origin_title")) {
                doubanMeta.setOriginTitle(jsonObject.getString("origin_title"));
            }
        }
        return doubanMeta;
    }


}
