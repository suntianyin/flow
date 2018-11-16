package com.apabi.flow.newspaper.chinadaily.util;

import com.apabi.flow.newspaper.chinanews.util.ChinanewsCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.model.Newspaper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/11/14 13:16
 **/
public class ChinaDailyCrawlUtils {
    /**
     * 请求切换ip重试次数
     */
    private static final int RETRY_COUNT = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinanewsCrawlUtils.class);

    /**
     * 生成HttpClient对象
     *
     * @return
     */
    private static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
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
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setProxy(httpHost).setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(requestRetryHandler).setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    /**
     * 生成请求
     *
     * @param url
     * @return
     */
    private static HttpGet generateHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "UM_distinctid=166c930e1924ea-01a30e4658d6a5-414a0029-1fa400-166c930e19345d; wdcid=43914c8819c517ab; pt_37a49e8b=uid=FBMiol83vzBlKfpwwdvgqg&nid=0&vid=24dbrDsd3g6IXgLxzJoQ1A&vn=6&pvn=1&sact=1542167952358&to_flag=0&pl=UcBDB0wmu1YhJt6MahJj2w*pt*1542167952358; pt_s_37a49e8b=vt=1542167952358&cad=; __auc=7c17ee231671628ceba2eda3166; CNZZDATA1975683=cnzz_eid%3D1242369739-1541037207-%26ntime%3D1542268509; wdlast=1542271669; wdses=6981e81b72933d4f");
        //httpGet.setHeader("Host", "www.chinanews.com");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据url获取每个页面的状态码
     *
     * @param url
     * @param cnrIpPoolUtils
     */
    public static int getStatusCode(String url, CnrIpPoolUtils cnrIpPoolUtils) {
        // 把状态码初始化为404
        int statusCode = HttpStatus.SC_NOT_FOUND;
        // 访问指定页码的页面
        HttpGet httpGet = generateHttpGet(url);
        // 设置请求头
        CloseableHttpResponse response = null;
        String host = cnrIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
        try {
            response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            int count = 0;
            while (statusCode != HttpStatus.SC_OK) {
                if (count == RETRY_COUNT) {
                    LOGGER.error("ChinaDaily抓取失败的页面：" + url);
                    break;
                }
                host = cnrIpPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                httpClient = getCloseableHttpClient(ip, port);
                response = httpClient.execute(httpGet);
                statusCode = response.getStatusLine().getStatusCode();
                count++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return statusCode;
    }

    /**
     * 获取专栏的页面数
     *
     * @param startPage
     * @param endPage
     * @param url
     * @return
     */
    public static int getPageNum(int startPage, int endPage, String url) {
        int pageNum = -1;
        // http://qiye.chinadaily.com.cn/node_53012073_2.htm
        // http://qiye.chinadaily.com.cn/node_53012073_%d.htm
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        int startPageCode = HttpStatus.SC_OK;
        int endPageCode = HttpStatus.SC_NOT_FOUND;

        while (startPageCode == HttpStatus.SC_OK && endPageCode == HttpStatus.SC_NOT_FOUND) {
            int middlePage = (startPage + endPage) >>> 1;
            String midUrl = String.format(url, middlePage);
            int statusCode = getStatusCode(midUrl, cnrIpPoolUtils);
            if (statusCode == HttpStatus.SC_OK) {
                startPage = middlePage;
            } else {
                endPage = middlePage;
            }
            if (endPage - startPage == 1) {
                return middlePage;
            }
        }
        return pageNum;
    }

    /**
     * 根据url抓取中国日报网的报纸内容
     *
     * @param cnrIpPoolUtils
     * @param url
     * @return
     */
    public static List<Newspaper> crawlByUrl(CnrIpPoolUtils cnrIpPoolUtils, String url) {
        List<Newspaper> newspaperList = new ArrayList<>();
        CloseableHttpResponse response = null;
        // 记录切换ip次数
        int count = 0;
        HttpGet httpGet = generateHttpGet(url);
        // 获取ip
        String host = cnrIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            while (statusCode != HttpStatus.SC_OK) {
                if (count >= RETRY_COUNT) {
                    break;
                }
                host = cnrIpPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                // 创建RequestConfig对象，切换ip
                httpClient = getCloseableHttpClient(ip, port);
                response = httpClient.execute(httpGet);
                statusCode = response.getStatusLine().getStatusCode();
                count++;
            }
            // 如果获取response成功才再做进一步解析
            if (statusCode == HttpStatus.SC_OK) {
                String html = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document document = Jsoup.parse(html);
                Elements newspaperElements = document.select("h3");
                for (Element newspaperElement : newspaperElements) {
                    Newspaper newspaper = new Newspaper();
                    String abstract_ = null;
                    Element abstractElement = newspaperElement.nextElementSibling();
                    if (abstractElement != null) {
                        abstract_ = abstractElement.text();
                    }
                    String href = newspaperElement.child(0).attr("href");
                    String title = newspaperElement.text();
                    newspaper.setTitle(title);
                    newspaper.setAbstract_(abstract_);
                    newspaper.setUrl(href);
                    newspaper.setSource("中国日报网");
                    newspaperList.add(newspaper);
                }
                LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + url + "成功...");
            } else {
                LOGGER.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + url + "XXXXXXXXXXX失败XXXXXXXXX...");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
        return newspaperList;
    }

    /**
     * 根据url抓取html内容
     *
     * @param url
     * @param cnrIpPoolUtils
     * @param newspaperDao
     */
    public static void crawlHtmlContentByUrl(String url, CnrIpPoolUtils cnrIpPoolUtils, NewspaperDao newspaperDao) {
        String originalUrl = url;
        if (!url.startsWith("http")) {
            url = "https://ent.chinadaily.com.cn/" + url;
        }
        CloseableHttpResponse response = null;
        try {
            String host = cnrIpPoolUtils.getIp();
            String ip = host.split(":")[0];
            String port = host.split(":")[1];
            CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
            HttpGet httpGet = generateHttpGet(url);
            response = httpClient.execute(httpGet);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String format = simpleDateFormat.format(date);
            if (response.getStatusLine().getStatusCode() == 200) {
                String html = EntityUtils.toString(response.getEntity(), "UTF-8");
                Newspaper newspaper = newspaperDao.findByUrl(originalUrl);
                newspaper.setHtmlContent(html);
                newspaperDao.update(newspaper);
                LOGGER.info(format + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + newspaper.getTitle() + "添加至数据库成功...");
            } else {
                LOGGER.info(format + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "抓取" + url + "添加至数据库XXXXXX失败XXXXX");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        String host = cnrIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient httpClient = getCloseableHttpClient(ip, port);
        List<Newspaper> newspaperList = crawlByUrl(cnrIpPoolUtils, "https://finance.chinadaily.com.cn/node_53005878_2.htm");
        for (Newspaper newspaper : newspaperList) {
            System.out.println(newspaper);
        }
    }
}
