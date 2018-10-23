package com.apabi.flow.crawlTask.util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author pipi
 * @Date 2018/10/22 16:15
 **/
public class GenerateCookieUtils {
    private static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setExpectContinueEnabled(false).setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        // 实例化CloseableHttpClient对象
        // 使用代理ip
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
        return client;
    }

    // 通过访问douban首页获取Cookie
    public static String crawlDoubanCookie(String ip, String port) throws IOException {
        Map<String, String> cookieValuePair = null;
        cookieValuePair = new HashMap<String, String>();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问豆瓣主题首页
        HttpGet httpGet = new HttpGet("http://www.douban.com");

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "www.douban.com");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            httpGet.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            Header[] headers = response.getHeaders("Set-Cookie");
            if (headers.length > 0) {
                for (Header header : headers) {
                    String headValue = header.getValue();
                    String[] valuePairs = headValue.split(";");
                    for (String valuePair : valuePairs) {
                        String[] split = valuePair.split("=");
                        if (split.length == 2) {
                            String key = split[0];
                            String value = split[1];
                            cookieValuePair.put(key, value);
                        }
                    }
                }
            }
        }
        String cookie = "";
        String suffixCookie = " __utmt=1; __utma=30149280.97956999.1540178056.1540178056.1540178056.1; __utmb=30149280.1.10.1540178056; __utmc=30149280; __utmz=30149280.1540178056.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; as=\"https://sec.douban.com/b?r=https%3A%2F%2Fbook.douban.com%2F\"; ps=y";
        if (!cookieValuePair.isEmpty()) {
            String ll = cookieValuePair.get("ll");
            String bid = cookieValuePair.get("bid");
            cookie = "ll=" + ll + "; bid=" + bid;
        }
        System.out.println(ip + ":" + port + "抓取的首页Cookie为：" + cookie);
        return cookie;
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\pirui\\Desktop\\flowPlatform\\src\\main\\resources\\properties\\doubanCookie.properties", true));
        Set<String> cookieSet = new HashSet<String>();
        for (int i = 0; i < 100; i++) {
            String ip = IpPoolUtils.getIp();
            String host = ip.split(":")[0];
            String port = ip.split(":")[1];
            String cookie = crawlDoubanCookie(host, port);
            if (StringUtils.isNotEmpty(cookie)) {
                cookieSet.add(cookie);
            }
        }
        for (String cookie : cookieSet) {
            writer.write(cookie);
            writer.newLine();
            writer.flush();
        }
        writer.close();
    }
}
