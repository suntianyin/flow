package com.apabi.flow.jd.util;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.jd.model.JdItemUrl;
import com.apabi.flow.jd.model.JdMetadata;
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
 * @Date 2018-12-3 10:25
 **/
public class CrawlJdUtils {
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
        httpGet.setHeader("Cookie", "shshshfpa=7905299e-5b3a-0cfa-9215-fdfce57c1cf3-1540891878; user-key=9971a423-c9cb-478e-a805-3097b3c17204; cn=0; ipLoc-djd=1-72-2799-0; mt_xid=V2_52007VwMWV1xRWloWTxBUBWADEVRdUFFfG0wQbAUzVBBTWF9XRhZOGQkZYlcUV0EIU1NMVRgJBjRWG1taUAYJSnkaXQVuHxJSQVtVSx9KEl8BbAcUYl9oUmocQBpVAWYFElFtWFdcGA%3D%3D; unpl=V2_ZzNtbRZTERFzDRZUeBEMAWJWRl5LUBcXfQ9CB30QCAVlABNVclRCFXwURlRnG10UZwIZXkZcRxZFCEdkexhdBGYBGlhLVXNILGYFVjBMFF9XMxFdcl9zFXENR11zGVUDbgsSWkJURRV9C0NQfhBsNWAzIgAaCBlMJVADBCUpWgFiABJYQ19DJXQ4R2Qwd11IZwcXXEtfQxxzAU5UfBlfA2cLEVhGUkoldDhF; __jdv=122270672|www.linkhaitao.com|t_1000039483_lh_r3zdyk|tuiguang|e5c574a129a54de286e3965b78e13209|1543544760013; _gcl_au=1.1.1593770614.1543544839; shshshfp=b137d069eff5db07c4c7e2cb2deadb74; shshshfpb=03609f76610530a46d64ed107d28e461bbee5260b1f5d31c15bd824e69; ipLocation=%u5317%u4EAC; areaId=1; 3AB9D23F7A4B3C9B=XM4GO3VQ5K52ZRUXYEQQ7FSNJDQBL3FS2UMPYYEFLNNTKIAPANGAZEYX6XXQNLTKJUP54NSVSASOAMRHIZV7OKM3XM; listck=bdffde4d9f7ddb77f4ab98e3649584b6; __jda=122270672.15408918789161271924548.1540891879.1543544760.1543888372.9; __jdb=122270672.3.15408918789161271924548|9.1543888372; __jdc=122270672; __jdu=15408918789161271924548");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据给定的url创建HttpGet对象
     *
     * @param url
     * @return
     */
    public static HttpGet generateJdItemHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Cookie", "shshshfpa=7905299e-5b3a-0cfa-9215-fdfce57c1cf3-1540891878; user-key=9971a423-c9cb-478e-a805-3097b3c17204; cn=0; ipLoc-djd=1-72-2799-0; mt_xid=V2_52007VwMWV1xRWloWTxBUBWADEVRdUFFfG0wQbAUzVBBTWF9XRhZOGQkZYlcUV0EIU1NMVRgJBjRWG1taUAYJSnkaXQVuHxJSQVtVSx9KEl8BbAcUYl9oUmocQBpVAWYFElFtWFdcGA%3D%3D; _gcl_au=1.1.1593770614.1543544839; unpl=V2_ZzNtbUQEER18WEVXLkxbBGIGE11KAEYUfAFOA3kYCARvVBBYclRCFXwURldnGFgUZwIZWEBcQR1FCEdkexhdBGYBGlhLVXNILGYFEH4QHVhXMxFdcl9zFXENR11zGVUDbgsSWkJURRV9C0NQfhBsNWAzIgAaCBlMJVADBCUpWgFiABJYQ19DJXQ4R2Qwd11IZwcXXEtfQxxzAU5UfBlfA2cLEVhGUkoldDhF; CCC_SE=ADC_daf6WSmbCaLCVVHJhM0le9jsXc3SqzxpRZotNN4AnOCyFTCZqEQAIWfiN%2bLXOokNk3c87xO%2byhnWoiQEIXKMZXMZsA%2fS7n2DCIgTQBngyWFQq3rippeA2SULQzeV%2fi4i7BR7jV%2f37vC0PcOsx%2frSDo9Ruf5D%2fpwB8zz5CASiB4V5celG43gvgteDA9HrMH048PM5oUSdBDHf1wHi10i5uUvqIvfyw3MpGnl%2fh8y6n9pvNTRBUmUmppsZOwFqJgXbzgti90sWlmHO8NGTQkgY5cNcILXttfc2ugllrO2wQe%2bb5QoIVJma4ssVWg8l8VFv4ikAHmVHnXXkYCP2H3y%2fJ%2fR3szK4M443ABWNny%2btAxcPBRSA7OxfNJ1kctvH5XkyweSACTXDsMpo00Ln0RHX1CBSNi3TN7nu0ZyO9JR4vt8wPSwBX%2fPKZhVXhAHApS7d81%2f0bBPiJuTWgQS8s10XWE9IVlnVguxW8wULxrtPoYDzrb4Mn7Nbr7z%2fXWgoSLQbFTYQhKtadIvwEQfkA117AKgAN8dRCKr3ssO7KKSO%2frVqsusktzsdXMZLAbke522%2fpfLWWftpfeuTXnCNXIgNEiR1RhWu2JdmvgfD1fMJpwQU5%2fhQHDZykSVhGmtcVsi5FmUsJypdvuHiP2lm7MoweBJ6MUie3GJy7aptXBBlkVs3LxbK94G5fVy0Jb7M1Vt9UFHBvkgGq%2bauLm6YtUFuf6ZG9%2brkJQiD%2bPChfT%2bWY%2bo%3d; __jdv=122270672|www.linkhaitao.com|t_1000039483_lh_ru48pl|tuiguang|7bc98a22dd6044019f40889f30e09f34|1543977820062; 3AB9D23F7A4B3C9B=XM4GO3VQ5K52ZRUXYEQQ7FSNJDQBL3FS2UMPYYEFLNNTKIAPANGAZEYX6XXQNLTKJUP54NSVSASOAMRHIZV7OKM3XM; shshshfp=b53e86a3d4351631403d60ad1144c61d; shshshsID=c96237e2060385d129f5f29465df85a4_4_1543994281120; shshshfpb=03609f76610530a46d64ed107d28e461bbee5260b1f5d31c15bd824e69; __jda=122270672.15408918789161271924548.1540891879.1543979768.1543994051.15; __jdb=122270672.4.15408918789161271924548|15.1543994051; __jdc=122270672; ipLocation=%u5317%u4EAC; areaId=1; __jdu=15408918789161271924548");
        httpGet.setHeader("Host", "item.jd.com");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        return httpGet;
    }

    /**
     * 根据给定的url和ip、port创建HttpGet对象
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static HttpGet generateHttpGet(String url, String ip, String port) {
        HttpGet httpGet = new HttpGet(url);
        HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Cookie", "shshshfpa=7905299e-5b3a-0cfa-9215-fdfce57c1cf3-1540891878; user-key=9971a423-c9cb-478e-a805-3097b3c17204; cn=0; ipLoc-djd=1-72-2799-0; mt_xid=V2_52007VwMWV1xRWloWTxBUBWADEVRdUFFfG0wQbAUzVBBTWF9XRhZOGQkZYlcUV0EIU1NMVRgJBjRWG1taUAYJSnkaXQVuHxJSQVtVSx9KEl8BbAcUYl9oUmocQBpVAWYFElFtWFdcGA%3D%3D; unpl=V2_ZzNtbRZTERFzDRZUeBEMAWJWRl5LUBcXfQ9CB30QCAVlABNVclRCFXwURlRnG10UZwIZXkZcRxZFCEdkexhdBGYBGlhLVXNILGYFVjBMFF9XMxFdcl9zFXENR11zGVUDbgsSWkJURRV9C0NQfhBsNWAzIgAaCBlMJVADBCUpWgFiABJYQ19DJXQ4R2Qwd11IZwcXXEtfQxxzAU5UfBlfA2cLEVhGUkoldDhF; __jdv=122270672|www.linkhaitao.com|t_1000039483_lh_r3zdyk|tuiguang|e5c574a129a54de286e3965b78e13209|1543544760013; _gcl_au=1.1.1593770614.1543544839; shshshfp=b137d069eff5db07c4c7e2cb2deadb74; shshshfpb=03609f76610530a46d64ed107d28e461bbee5260b1f5d31c15bd824e69; ipLocation=%u5317%u4EAC; areaId=1; 3AB9D23F7A4B3C9B=XM4GO3VQ5K52ZRUXYEQQ7FSNJDQBL3FS2UMPYYEFLNNTKIAPANGAZEYX6XXQNLTKJUP54NSVSASOAMRHIZV7OKM3XM; listck=bdffde4d9f7ddb77f4ab98e3649584b6; __jda=122270672.15408918789161271924548.1540891879.1543544760.1543888372.9; __jdb=122270672.3.15408918789161271924548|9.1543888372; __jdc=122270672; __jdu=15408918789161271924548");
        httpGet.setHeader("Proxy-Connection", "keep-alive");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        httpGet.setConfig(requestConfig);
        return httpGet;
    }

    /**
     * 根据页码抓取不同页码中的商品链接
     *
     * @param pageUrl
     * @param nlcIpPoolUtils
     * @return
     */
    public static List<String> crawlItemUrlsByPageUrl(String pageUrl, NlcIpPoolUtils nlcIpPoolUtils) {
        List<String> urlList = new ArrayList<>();
        String host = nlcIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        HttpGet httpGet = generateHttpGet(pageUrl);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            Elements items = document.select("div[class='p-name']");
            for (Element item : items) {
                String itemHref = item.child(0).attr("href");
                urlList.add(itemHref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlList;
    }

    /**
     * 根据pageUrl获取该分类的页码
     *
     * @param pageUrl
     * @param nlcIpPoolUtils
     * @return
     */
    public static int crawlItemUrlPageNumByPageUrl(String pageUrl, NlcIpPoolUtils nlcIpPoolUtils) {
        int pageNum = 0;
        String host = nlcIpPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        HttpGet httpGet = generateHttpGet(pageUrl);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            Elements pageElements = document.select("span[class='p-skip']");
            if (pageElements != null && pageElements.size() > 0) {
                pageNum = Integer.parseInt(pageElements.get(0).child(0).child(0).text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageNum;
    }

    /**
     * 根据给定的url和ip、port抓取该url中item的url
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static List<JdItemUrl> crawlItemUrlByPageUrl(String url, String ip, String port) throws Exception {
        List<JdItemUrl> jdItemUrlList = new ArrayList<>();
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getCloseableHttpClient(ip, port);
            HttpGet httpGet = generateHttpGet(url);
            response = client.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String html = EntityUtils.toString(response.getEntity());
                Document document = Jsoup.parse(html);
                Elements itemElements = document.select("div[class='p-name']");
                if (itemElements != null && itemElements.size() > 0) {
                    for (Element itemElement : itemElements) {
                        Element aElement = itemElement.child(0);
                        if (aElement != null) {
                            String itemHref = aElement.attr("href");
                            JdItemUrl jdItemUrl = new JdItemUrl();
                            jdItemUrl.setUrl(itemHref);
                            jdItemUrl.setStatus("0");
                            jdItemUrlList.add(jdItemUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jdItemUrlList;
    }

    /**
     * 根据给定的url和ip、port抓取JdMetadata
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static JdMetadata crawlJdMetadataByUrl(String url, String ip, String port) throws Exception {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        JdMetadata jdMetadata = null;
        try {
            client = getCloseableHttpClient(ip, port);
            HttpGet httpGet = generateJdItemHttpGet(url);
            response = client.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String html = EntityUtils.toString(response.getEntity());
                jdMetadata = generateJdMetadataFromHtml(html);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jdMetadata;
    }

    /**
     * 根据抓取的html解析出JdMetadata
     *
     * @param html
     * @return
     */
    private static JdMetadata generateJdMetadataFromHtml(String html) {
        JdMetadata jdMetadata = null;
        if (StringUtils.isNotEmpty(html)) {
            jdMetadata = new JdMetadata();
            Document document = Jsoup.parse(html);
            String title = document.select("div[class='sku-name']").get(0).text();
            Date currentTime = new Date();
            Date createTime = currentTime;
            Date updateTime = currentTime;
            String editionOrder = null;
            String jdItemId = null;
            String binding = null;
            String issuedDate = null;
            String pages = null;
            String language = null;
            String format = null;
            String brand = null;
            String isbn13 = null;
            String publisher = null;
            Elements parameterElements = document.select("ul[class='p-parameter-list']").get(0).children();
            for (Element parameterElement : parameterElements) {
                if (parameterElement.text().contains("出版社：")) {
                    publisher = parameterElement.text();
                    publisher = publisher.split("：")[1];
                }
                if (parameterElement.text().contains("版次：")) {
                    editionOrder = parameterElement.text();
                    editionOrder = editionOrder.split("：")[1];
                }
                if (parameterElement.text().contains("商品编码：")) {
                    jdItemId = parameterElement.text();
                    jdItemId = jdItemId.split("：")[1];
                }
                if (parameterElement.text().contains("包装：")) {
                    binding = parameterElement.text();
                    binding = binding.split("：")[1];
                }
                if (parameterElement.text().contains("出版时间：")) {
                    issuedDate = parameterElement.text();
                    issuedDate = issuedDate.split("：")[1];
                }
                if (parameterElement.text().contains("页数：")) {
                    pages = parameterElement.text();
                    pages = pages.split("：")[1];
                }
                if (parameterElement.text().contains("正文语种：")) {
                    language = parameterElement.text();
                    language = language.split("：")[1];
                }
                if (parameterElement.text().contains("开本：")) {
                    format = parameterElement.text();
                    format = format.split("：")[1];
                }
                if (parameterElement.text().contains("品牌：")) {
                    brand = parameterElement.text();
                    brand = brand.split("：")[1];
                }
                if (parameterElement.text().contains("ISBN：")) {
                    isbn13 = parameterElement.text();
                    isbn13 = isbn13.split("：")[1];
                }
            }
            jdMetadata.setTitle(title);
            jdMetadata.setJdItemId(jdItemId);
            jdMetadata.setEditionOrder(editionOrder);
            jdMetadata.setBinding(binding);
            jdMetadata.setIssuedDate(issuedDate);
            if (pages != null) {
                jdMetadata.setPages(Integer.parseInt(pages));
            } else {
                jdMetadata.setPages(0);
            }
            jdMetadata.setLanguage(language);
            jdMetadata.setFormat(format);
            jdMetadata.setBrand(brand);
            jdMetadata.setIsbn13(isbn13);
            jdMetadata.setPublisher(publisher);
            jdMetadata.setMetaId(null);
            jdMetadata.setUpdateTime(updateTime);
            jdMetadata.setCreateTime(createTime);
        }
        return jdMetadata;
    }
}
