package com.apabi.flow.nlcmarc_category.util;

import com.apabi.flow.crawlTask.nlc_category.category.NlcBookMarcCategoryConsumer;
import com.apabi.flow.crawlTask.nlc_category.category.NlcBookMarcCategoryPageUrlConsumer;
import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/11/21 10:34
 **/
public class CrawlNlcMarcCategoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlNlcMarcCategoryUtil.class);
    private static Random random = new Random();
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 国图分类分页抓取只能抓取99页
     */
    private static final int AVAILABLE_PAGE_NUM = 99;
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
     * 根据categoryId获取该category的页码
     *
     * @param categoryId 需要抓取的category的id
     * @param ip         代理ip
     * @param port       代理ip的端口号
     * @return 页码
     * @throws IOException
     * @throws InterruptedException
     */
    public static Integer getCategoryPageNumber(String categoryId, String ip, String port) throws IOException, InterruptedException {
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问国图首页
        HttpGet httpGet1 = generateHttpGet("http://opac.nlc.cn/F/");
        CloseableHttpResponse response1 = null;
        // 访问国图首页，获取token
        try {
            response1 = client.execute(httpGet1);
            Thread.sleep(300);
        } catch (IOException e) {
            LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上国图首页...");
            //e.printStackTrace();
            httpGet1.setHeader("Connection", "close");
            httpGet1.releaseConnection();
            httpGet1.abort();
            throw new IOException(e);
        }
        // 防封IP
        Thread.sleep(random.nextInt(300) + 200);
        // 获取response1的实体类
        HttpEntity entity1 = response1.getEntity();
        String html = null;
        try {
            html = EntityUtils.toString(entity1, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        } finally {
            EntityUtils.consumeQuietly(entity1);
            // 关闭response1
            try {
                EntityUtils.consume(entity1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 利用Jsoup解析html
        Document document = Jsoup.parse(html);
        String string = document.select("form[name='form1']").attr("action");
        // 从页面中解析出标识码
        String tokenCode = string.substring(string.lastIndexOf("/") + 1, string.length());
        String first = "http://opac.nlc.cn/F/" + tokenCode + "?func=find-b&request=" + categoryId;
        String second = "%3F&local_base=NLC01&find_code=CLC";
        // 拼接查询首页的url
        String firstURL = first + second;
        // 释放httpGet1
        httpGet1.releaseConnection();
        HttpGet httpGet2 = generateHttpGet(firstURL);
        // 防封IP
        Thread.sleep(random.nextInt(300) + 350);
        CloseableHttpResponse response2 = null;
        try {
            response2 = client.execute(httpGet2);
        } catch (IOException e) {
            LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + firstURL + "上的页码失败...");
            //e.printStackTrace();
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
        Element div = document2.select("div[id='fcache']").parents().get(1);
        String pageContent = div.text();
        String page = pageContent.substring(pageContent.indexOf('f') + 1, pageContent.indexOf("(")).trim();
        Integer pageNum = Integer.parseInt(page) / 10 + 1;
        LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "获取" + categoryId + "页码成功，页码为：" + pageNum);
        return pageNum;
    }

    /**
     * 根据categoryCode抓取该类别下所有marc数据
     *
     * @param categoryCode
     * @param nlcIpPoolUtils
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static void crawlNlcBookMarcByCategoryCode(String categoryCode, int pageNum, NlcIpPoolUtils nlcIpPoolUtils, NlcBookMarcDao nlcBookMarcDao) throws IOException {
        Random random = new Random();
        String host = nlcIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        // 实例化CloseableHttpClient对象
        final CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问国图首页
        HttpGet indexHttpGet = generateHttpGet("http://opac.nlc.cn/F/");
        // 访问国图首页，获取token；当该ip无法访问国图首页时，切换其他ip访问国图首页
        CloseableHttpResponse indexResponse = null;
        int statusCode = HttpStatus.SC_NOT_FOUND;
        int retryCount = 0;
        while (statusCode != HttpStatus.SC_OK) {
            if (retryCount >= RETRY_COUNT) {
                break;
            }
            try {
                host = nlcIpPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                // 切换ip
                indexHttpGet = switchIp(indexHttpGet, ip, port);
                // 防封ip
                Thread.sleep(300);
                // 访问国图首页
                indexResponse = client.execute(indexHttpGet);
                statusCode = HttpStatus.SC_OK;
            } catch (IOException e) {
                retryCount++;
                LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上国图首页...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (indexResponse != null) {
                statusCode = indexResponse.getStatusLine().getStatusCode();
                retryCount++;
            }
        }
        // 防封IP
        try {
            Thread.sleep(random.nextInt(300) + 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取indexResponse的实体类
        HttpEntity entity1 = indexResponse.getEntity();
        String indexHtml = null;
        try {
            indexHtml = EntityUtils.toString(entity1, "UTF-8");
        } catch (IOException e) {
            // 如果报错再重试一次
            try {
                indexHtml = EntityUtils.toString(entity1, "UTF-8");
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new IOException(e1);
            }
            e.printStackTrace();
        } finally {
            EntityUtils.consumeQuietly(entity1);
            try {
                EntityUtils.consume(entity1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (indexHtml != null) {
            // 利用Jsoup解析html
            Document indexDocument = Jsoup.parse(indexHtml);
            String tokenUrl = indexDocument.select("form[name='form1']").attr("action");
            // 从页面中解析出标识码
            String tokenCode = tokenUrl.substring(tokenUrl.lastIndexOf("/") + 1, tokenUrl.length());
            // 如果从首页中解析出标识码才继续进行
            if (StringUtils.isNotEmpty(tokenCode)) {
                String var1 = "http://opac.nlc.cn/F/";
                String var2 = "?func=find-b&request=" + categoryCode + "?&local_base=NLC01&find_code=CLC";
                // 类别首页链接
                String codeHomePageUrl = var1 + tokenCode + var2;
                HttpGet codeHomePageUrlHttpGet = generateHttpGet(codeHomePageUrl);
                try {
                    // 防封ip
                    Thread.sleep(random.nextInt(300) + 200);
                    // 务必访问类别首页：将token与访问类别绑定在一起
                    CloseableHttpResponse categoryHomeResponse = client.execute(codeHomePageUrlHttpGet);
                    // 如果没能访问类别首页成功则切ip重试
                    if (categoryHomeResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        host = nlcIpPoolUtils.getIp();
                        ip = host.split(":")[0];
                        port = host.split(":")[1];
                        codeHomePageUrlHttpGet = switchIp(codeHomePageUrlHttpGet, ip, port);
                        client.execute(codeHomePageUrlHttpGet);
                    }
                } catch (Exception e) {
                    LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上分类码为" + categoryCode + "的首页...");
                }
                ExecutorService executorService = Executors.newFixedThreadPool(4 * CPU_COUNT);
                if (pageNum >= AVAILABLE_PAGE_NUM) {
                    pageNum = AVAILABLE_PAGE_NUM - 1;
                }
                // 分页链接队列
                LinkedBlockingQueue<String> pageUrlQueue = new LinkedBlockingQueue<>();
                // marc数据链接队列
                LinkedBlockingQueue<String> marcHrefQueue = new LinkedBlockingQueue<>();
                for (int i = 1; i <= pageNum; i++) {
                    // 翻页URL
                    String var3 = codeHomePageUrl.split("find-b")[0];
                    String var4 = "short-jump&jump=" + i + "1";
                    // 每页的url
                    String pageUrl = var3 + var4;
                    try {
                        pageUrlQueue.put(pageUrl);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 标记队列大小
                int pageUrlQueueSize = pageUrlQueue.size();
                CountDownLatch pageUrlCountDownLatch = new CountDownLatch(pageUrlQueueSize);
                ExecutorService pageUrlExecutorService = Executors.newFixedThreadPool(10 * CPU_COUNT);
                NlcBookMarcCategoryPageUrlConsumer consumer = new NlcBookMarcCategoryPageUrlConsumer(pageUrlQueue, client, marcHrefQueue, nlcIpPoolUtils, pageUrlCountDownLatch);
                for (int i = 0; i < pageUrlQueueSize; i++) {
                    pageUrlExecutorService.execute(consumer);
                }
                try {
                    pageUrlCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pageUrlExecutorService.shutdown();
                CountDownLatch marcHrefCountDownLatch = new CountDownLatch(marcHrefQueue.size());
                for (String marcHref : marcHrefQueue) {
                    NlcBookMarcCategoryConsumer categoryConsumer = new NlcBookMarcCategoryConsumer(marcHref, nlcIpPoolUtils, marcHrefCountDownLatch, client, nlcBookMarcDao);
                    executorService.execute(categoryConsumer);
                }
                try {
                    marcHrefCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executorService.shutdown();
            }
        }
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
        httpGet.setHeader("Cookie", "Hm_lvt_199be991c3ca69223b0d946b04112648=1542681725; Hm_lvt_2cb70313e397e478740d394884fb0b8a=1540976739,1540977821,1542620301,1542676935");
        httpGet.setHeader("Host", "opac.nlc.cn");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据nlcIpPoolUtils池中的ip切换ip
     *
     * @param httpGet
     * @param ip
     * @param port
     * @return
     */
    public static HttpGet switchIp(HttpGet httpGet, String ip, String port) {
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        httpGet.setConfig(requestConfig);
        return httpGet;
    }


    /**
     * 根据pageUrl抓取页面中的html
     *
     * @param pageUrl
     * @param nlcIpPoolUtils
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static String crawlHtmlPageByPageUrl(CloseableHttpClient client, String pageUrl, NlcIpPoolUtils nlcIpPoolUtils) throws InterruptedException, IOException {
        String host = nlcIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        Random random = new Random();
        HttpGet pageHttpGet = CrawlNlcMarcCategoryUtil.generateHttpGet(pageUrl);
        // 每执行一次切换一次ip
        pageHttpGet = CrawlNlcMarcCategoryUtil.switchIp(pageHttpGet, ip, port);
        // 防封ip
        Thread.sleep(random.nextInt(300) + 200);
        client = CrawlNlcMarcCategoryUtil.getCloseableHttpClient(ip, port);
        //pageResponse = this.client.execute(pageHttpGet);
        HttpResponse pageResponse = client.execute(pageHttpGet);
        String pageHtml = EntityUtils.toString(pageResponse.getEntity(), "UTF-8");
        return pageHtml;
    }

    public static void main(String[] args) {
        //crawlNlcBookMarcByCategoryCode("B843", 11, new NlcIpPoolUtils(), null);
    }

}
