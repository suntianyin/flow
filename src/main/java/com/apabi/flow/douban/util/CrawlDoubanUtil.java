package com.apabi.flow.douban.util;

import com.apabi.flow.crawlTask.util.DoubanCookieUtils;
import com.apabi.flow.crawlTask.util.UserAgentUtils;
import com.apabi.flow.douban.model.DoubanMeta;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/15 18:03
 **/
public class CrawlDoubanUtil {
    private static Logger logger = LoggerFactory.getLogger(CrawlDoubanUtil.class);

    private static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
        // SocketConfig
        // SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(10000).build();
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(10000).setSoLinger(60).setSoKeepAlive(true).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(config).build();
        return client;
    }

    // 从douban根据抓取列表获取该页面中的douban数据
    public static List<String> crawlDoubanIdList(String url, String ip, String port) {
        List<String> doubanMetaIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(url)) {
            // 实例化CloseableHttpClient对象
            CloseableHttpClient client = getCloseableHttpClient(ip, port);
            // 访问豆瓣主题首页
            HttpGet httpGet = new HttpGet(url);
            // 请求配置
            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.setHeader("Cache-Control", "max-age=0");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("Host", "book.douban.com");
            httpGet.setHeader("Upgrade-Insecure-Request", "1");
//            httpGet.setHeader("Cookie", "ll=\"108288\"; bid=AOJiWkefK-E; __utmt=1; __utma=30149280.97956999.1540178056.1540178056.1540178056.1; __utmb=30149280.1.10.1540178056; __utmc=30149280; __utmz=30149280.1540178056.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; as=\"https://sec.douban.com/b?r=https%3A%2F%2Fbook.douban.com%2F\"; ps=y");
            httpGet.setHeader("Cookie", DoubanCookieUtils.getCookie() + "; __utmt=1; __utma=30149280.97956999.1540178056.1540178056.1540178056.1; __utmb=30149280.1.10.1540178056; __utmc=30149280; __utmz=30149280.1540178056.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; as=\"https://sec.douban.com/b?r=https%3A%2F%2Fbook.douban.com%2F\"; ps=y");
//            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            httpGet.setHeader("User-Agent", UserAgentUtils.getUserAgent());
            //httpGet.setHeader("Referer", "https://accounts.douban.com/login?alias=469250376%40qq.com&redir=https%3A%2F%2Fbook.douban.com%2Ftag%2F%25E4%25BA%25BA%25E6%2596%2587%3Ftype%3DR&source=None&error=1027");
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
                //logger.error("线程" + Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上豆瓣主题主页");
                //e.printStackTrace();
//                try {
//
//                } catch (IOException e1) {
//                    //e1.printStackTrace();
//                }
                //throw new IOException();
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

    // 根据doubanId并切换ip解析出DoubanMeta对象
    public static DoubanMeta crawlDoubanMetaById(String id, String ip, String port) {
        DoubanMeta doubanMeta = new DoubanMeta();
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        String url = "https://api.douban.com/v2/book/" + id;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        // 不可以加这个请求头，如果加上则走book.douban.com服务器，api应该走api.douban.com服务器
        // httpGet.setHeader("Host", "book.douban.com");
        // httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        httpGet.setHeader("User-Agent", UserAgentUtils.getUserAgent());
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            if (response != null) {
                String html = EntityUtils.toString(response.getEntity());
                if (StringUtils.isNotEmpty(html)) {
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
            }
        } catch (IOException e) {
            //e.printStackTrace();
            //throw new IOException();
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

}
