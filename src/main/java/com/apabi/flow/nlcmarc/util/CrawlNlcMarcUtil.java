package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.crawlTask.exception.NoSuchIsbnException;
import com.apabi.flow.crawlTask.util.UserAgentUtils;
import org.apache.http.HttpEntity;
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

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * @author pirui
 */
public class CrawlNlcMarcUtil {
    private static Random random = new Random();
    // 重试次数
    private static final int RETRY_COUNT = 3;

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
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(requestRetryHandler).setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    /**
     * 从国图根据isbn或者isbn13获取marc数据内容
     *
     * @param ISBN 需要抓取的ISBN
     * @param ip   代理ip
     * @param port 代理ip的端口号
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws NoSuchIsbnException
     */
    public static String crawlNlcMarc(String ISBN, String ip, String port) throws IOException, InterruptedException, NoSuchIsbnException {
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问国图首页
        HttpGet httpGet1 = new HttpGet("http://opac.nlc.cn/F/");
        httpGet1.setHeader("User-Agent", UserAgentUtils.getUserAgent());
        httpGet1.setHeader("Connection", "keep-alive");
        httpGet1.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet1.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet1.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet1.setHeader("Host", "opac.nlc.cn");
        httpGet1.setHeader("Cookie", "Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540534556,1540776061,1540781688,1540781732");
        httpGet1.setHeader("Upgrade-Insecure-Requests", "1");
        CloseableHttpResponse response1 = null;
        // 访问国图首页，获取标识码
        try {
            response1 = client.execute(httpGet1);
            Thread.sleep(300);
        } catch (IOException e) {
            e.printStackTrace();
            httpGet1.setHeader("Connection", "close");
            httpGet1.releaseConnection();
            httpGet1.abort();
            client.close();
            throw new IOException(e);
        }
        // 防封IP
        Thread.sleep(random.nextInt(300) + 200);
        // 获取response1的实体类
        HttpEntity entity1 = response1.getEntity();
        String html = null;
        try {
            html = EntityUtils.toString(entity1, "GBK");
        } catch (IOException e) {
            e.printStackTrace();
            client.close();
            throw new IOException(e);
        } finally {
            EntityUtils.consumeQuietly(entity1);
            // 关闭response1
            try {
                EntityUtils.consume(entity1);
                response1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 利用Jsoup解析html
        Document document = Jsoup.parse(html);
        String string = document.select("form[name='form1']").attr("action");
        String first = "http://opac.nlc.cn/F/";
        // 从页面中解析出标识码
        String tokenCode = string.substring(string.lastIndexOf("/") + 1, string.length());
        String third = "?func=find-b&find_code=ISB&request=" + ISBN + "&local_base=NLC01&filter_code_1=WLN&filter_request_1=&filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFM&filter_request_4=&filter_code_5=WSL&filter_request_5=";
        // 通过ISBN查询
        String firstURL = first + tokenCode + third;
        httpGet1.releaseConnection();
        HttpGet httpGet2 = new HttpGet(firstURL);
        httpGet2.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet2.setHeader("Connection", "keep-alive");
        httpGet2.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet2.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet2.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet2.setHeader("Host", "opac.nlc.cn");
        httpGet2.setHeader("Cookie", "Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540534556,1540776061,1540781688,1540781732");
        httpGet2.setHeader("Upgrade-Insecure-Requests", "1");
        // 防封IP
        Thread.sleep(random.nextInt(300) + 350);
        CloseableHttpResponse response2 = null;
        try {
            response2 = client.execute(httpGet2);
        } catch (IOException e) {
            e.printStackTrace();
            httpGet2.setHeader("Connection", "close");
            httpGet2.releaseConnection();
            httpGet2.abort();
            client.close();
            throw new IOException(e);
        }
        HttpEntity entity2 = response2.getEntity();
        String html2 = null;
        try {
            html2 = EntityUtils.toString(entity2, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
            throw new IOException(e);
        } finally {
            EntityUtils.consumeQuietly(entity2);
            EntityUtils.consume(entity2);
            // 关闭response2
            try {
                response2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Document document2 = Jsoup.parse(html2);
        String href = document2.select("a[title='保存/邮寄']").attr("href");
        String doc_number = "";
        try {
            // 获取doc_number确定下载哪本书
            doc_number = href.substring(href.indexOf("doc_number=") + 11, href.indexOf("doc_number=") + 20);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
            throw new NoSuchIsbnException("国图数据库中没有该ISBN:" + ISBN);
        }
        String first1 = first;
        String second1 = tokenCode;
        String third1 = "?func=full-mail&doc_library=NLC01&doc_number=" + doc_number + "&option_type=&format=997&encoding=NONE&SUBJECT=&NAME=&EMAIL=&text=&x=90&y=9";
        String finalURL2 = first1 + second1 + third1;
        httpGet2.releaseConnection();
        HttpGet httpGet3 = new HttpGet(finalURL2);
        httpGet3.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet3.setHeader("Connection", "keep-alive");
        httpGet3.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet3.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet3.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet3.setHeader("Host", "opac.nlc.cn");
        httpGet3.setHeader("Cookie", "Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540534556,1540776061,1540781688,1540781732");
        httpGet3.setHeader("Upgrade-Insecure-Requests", "1");
        // 防封IP
        Thread.sleep(random.nextInt(500) + 350);
        CloseableHttpResponse response3 = null;
        try {
            response3 = client.execute(httpGet3);
        } catch (IOException e) {
            e.printStackTrace();
            httpGet3.setHeader("Connection", "close");
            httpGet3.releaseConnection();
            httpGet3.abort();
            //client.close();
            throw new IOException(e);
        }
        HttpEntity entity3 = response3.getEntity();
        String html3 = null;
        try {
            html3 = EntityUtils.toString(entity3, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            EntityUtils.consumeQuietly(entity3);
            EntityUtils.consume(entity3);
            //client.close();
            throw new IOException(e);
        } finally {
            // 关闭response3
            try {
                response3.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        String href1 = Jsoup.parse(html3).select("p[class='text3'] a").attr("href");
        httpGet3.releaseConnection();
        HttpGet httpGet4 = new HttpGet(href1);
        httpGet4.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet4.setHeader("Connection", "keep-alive");
        httpGet4.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet4.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet4.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet4.setHeader("Host", "opac.nlc.cn");
        httpGet4.setHeader("Cookie", "Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540534556,1540776061,1540781688,1540781732");
        httpGet4.setHeader("Upgrade-Insecure-Requests", "1");
        Thread.sleep(350);
        CloseableHttpResponse response4 = null;
        try {
            response4 = client.execute(httpGet4);
        } catch (IOException e) {
            e.printStackTrace();
            httpGet4.setHeader("Connection", "close");
            httpGet4.releaseConnection();
            httpGet4.abort();
            client.close();
            throw new IOException(e);
        }
        HttpEntity entity4 = response4.getEntity();
        String iso = null;
        try {
            iso = EntityUtils.toString(entity4, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            client.close();
            throw new IOException(e);
        } finally {
            EntityUtils.consumeQuietly(entity4);
            EntityUtils.consume(entity4);
            // 关闭response4
            try {
                response4.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        }
        client.close();
        return iso;
    }

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchIsbnException {
        String iso = CrawlNlcMarcUtil.crawlNlcMarc("7-5407-2053-0", "212.111.2.220", "53281");
        System.out.println(iso);
    }
}
