package com.apabi.flow.douban.util;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.model.AmazonMeta;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
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
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/17 11:09
 **/
public class CrawlAmazonUtils {
    private static Logger logger = LoggerFactory.getLogger(CrawlAmazonUtils.class);
    private static final int RETRY_COUNT = 4;

    /**
     * 创建HttpClient对象
     *
     * @param ip   代理ip
     * @param port 代理ip的端口号
     * @return HttpClient对象
     */
    private static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        // SocketConfig
        // SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(10000).setSoKeepAlive(false).build();
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(10000).setSoLinger(60).setSoKeepAlive(true).setSoKeepAlive(false).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).build();
        return client;
    }

    /**
     * 从amazon根据抓取列表获取该页面中的amazon数据，如果抓取不到，则切换ip重试，重试次数为3.
     *
     * @param url            专题分页url
     * @param ipPoolUtils    获取ip和port的工具类
     * @param countDownLatch 线程安全计数器
     * @return 抓取回来的id列表
     */
    public static List<String> crawlAmazonIdList(String url, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        long startTime = System.currentTimeMillis();
        int retryCount = 0;
        List<String> idList = new ArrayList<>();
        String ip = "";
        String port = "";
        // 访问amazon主题首页
        HttpGet httpGet = new HttpGet(url);
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = null;
        // 访问amazon分类首页
        CloseableHttpResponse response = null;
        try {
            int statusCode = HttpStatus.SC_NOT_FOUND;
            // 如果失败了，则切换ip重试
            while (statusCode != HttpStatus.SC_OK) {

                if (retryCount == RETRY_COUNT) {
                    break;
                }
                // 切换ip计数
                String host = ipPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                // 设置代理ip
                HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
                // 请求配置
                RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
                httpGet.setConfig(config);
                httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
                httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                httpGet.setHeader("Cache-Control", "max-age=0");
                httpGet.setHeader("Connection", "keep-alive");
                httpGet.setHeader("Cookie", "x-wl-uid=1Sej1iP4/xSgeBoROi9739Cfoj1fYJu1Gd7CoVMzGC1X/mh56G/VkWH0my91c1w+Wpd8VRNxBniE=; session-token=1BVVJTP1+RaldFDZsmiNmTj4usN3+B0bryOUeoJsmBgXnwczc5CJ0TC634d/OdZZqiViu3EIXxwlf+W7lesdUQL7jbBOStBPPbYw40J92gjbcuXEUB4wH+zQk+eIvzPG0Bi5JCPfNZPImFbPpUO6+tH/7uzNH4Ir4l85D57VPmEnEfFkfsMOXX2HsDR+Z0+0; ubid-acbcn=458-6935151-3884818; session-id-time=2082729601l; session-id=457-1376902-6982932; csm-hit=tb:XPFXH94D44VDXC8TG5T4+s-XPFXH94D44VDXC8TG5T4|1542683794538&adb:adblk_no");
                httpGet.setHeader("Host", "www.amazon.cn");
                httpGet.setHeader("Upgrade-Insecure-Request", "1");
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
                httpGet.setConfig(config);
                client = getCloseableHttpClient(ip, port);
                try {
                    response = client.execute(httpGet);
                    if (response != null) {
                        statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == HttpStatus.SC_OK) {
                            String html = EntityUtils.toString(response.getEntity());
                            Document document = Jsoup.parse(html);
                            Elements elements = document.select("li[class='s-result-item celwidget  ']");
                            for (Element element : elements) {
                                String id = element.attr("data-asin");
                                idList.add(id);
                            }
                        }
                    }
                } catch (IOException e) {
                }
                retryCount++;
            }
            long endTime = System.currentTimeMillis();
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "；列表大小为：" + idList.size() + "；剩余列表数：" + countDownLatch.getCount() + "；耗时为：" + (endTime - startTime) / 1000 + "秒");
        } catch (Exception e) {
        } finally {
            httpGet.releaseConnection();
            httpGet.abort();
            if (response != null) {
                try {
                    //会自动释放连接
                    EntityUtils.consume(response.getEntity());
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
        return idList;
    }

    /**
     * 根据amazonId抓取amazon数据，如果失败，则切换ip重试，重试次数为3.
     *
     * @param id amazonId
     * @return 抓取返回的amazon数据
     */
    public static AmazonMeta crawlAmazonMetaById(String id, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        long startTime = System.currentTimeMillis();
        AmazonMeta amazonMeta = new AmazonMeta();
        String ip = "";
        String port = "";
        String url = "https://www.amazon.cn/dp/" + id + "/ref=sr_1_9?s=books&ie=UTF8&qid=1542259479&sr=1-9";
        // 访问amazonId的详情页
        HttpGet httpGet = new HttpGet(url);
        int statusCode = HttpStatus.SC_NOT_FOUND;
        int retryCount = 0;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            while (statusCode != HttpStatus.SC_OK) {
                if (retryCount == RETRY_COUNT) {
                    break;
                }
                String host = ipPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
                // 实例化CloseableHttpClient对象
                client = getCloseableHttpClient(ip, port);
                HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
                // 请求配置
                RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
                httpGet.setConfig(config);
                httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
                httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                httpGet.setHeader("Cache-Control", "max-age=0");
                httpGet.setHeader("Connection", "keep-alive");
                httpGet.setHeader("Cookie", "x-wl-uid=1Sej1iP4/xSgeBoROi9739Cfoj1fYJu1Gd7CoVMzGC1X/mh56G/VkWH0my91c1w+Wpd8VRNxBniE=; session-token=1BVVJTP1+RaldFDZsmiNmTj4usN3+B0bryOUeoJsmBgXnwczc5CJ0TC634d/OdZZqiViu3EIXxwlf+W7lesdUQL7jbBOStBPPbYw40J92gjbcuXEUB4wH+zQk+eIvzPG0Bi5JCPfNZPImFbPpUO6+tH/7uzNH4Ir4l85D57VPmEnEfFkfsMOXX2HsDR+Z0+0; ubid-acbcn=458-6935151-3884818; session-id-time=2082729601l; session-id=457-1376902-6982932; csm-hit=tb:XPFXH94D44VDXC8TG5T4+s-XPFXH94D44VDXC8TG5T4|1542683794538&adb:adblk_no");
                httpGet.setHeader("Host", "www.amazon.cn");
                httpGet.setHeader("Upgrade-Insecure-Request", "1");
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
                try {
                    response = client.execute(httpGet);
                    if (response != null) {
                        statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == HttpStatus.SC_OK) {
                            String html = EntityUtils.toString(response.getEntity());
                            amazonMeta = parseAmazonByHtml(html);
                        }
                    }
                } catch (IOException e) {
                }
                retryCount++;
            }
            long endTime = System.currentTimeMillis();
            if (StringUtils.isNotEmpty(amazonMeta.getAmazonId())) {
                logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取：" + id + "成功；列表中剩余：" + countDownLatch.getCount() + "个数据...；耗时为：" + (endTime - startTime) / 1000 + "秒");

            } else {
                logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取：" + id + "失败；列表中剩余：" + countDownLatch.getCount() + "个数据...；耗时为：" + (endTime - startTime) / 1000 + "秒");
            }
        } catch (Exception e) {
        } finally {
            httpGet.releaseConnection();
            httpGet.abort();
            if (response != null) {
                try {
                    //会自动释放连接
                    EntityUtils.consume(response.getEntity());
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
        if (amazonMeta != null) {
            // 设置出版日期
            String issuedDate = amazonMeta.getIssuedDate();
            if (StringUtils.isNotEmpty(issuedDate)) {
                issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
                if (issuedDate.contains(" 00:00:00")) {
                    issuedDate = issuedDate.replaceAll(" 00:00:00", "");
                }
            }
            amazonMeta.setIssuedDate(issuedDate);
            // 设置创建时间和更新时间
            amazonMeta.setCreateTime(new Date());
            amazonMeta.setUpdateTime(new Date());
            // 设置已经抓取
            amazonMeta.setHasCrawled(1);
        }
        return amazonMeta;
    }


    /**
     * 根据抓取的html内容解析生成amazon数据
     *
     * @param html html内容
     * @return amazon数据
     */
    private static AmazonMeta parseAmazonByHtml(String html) {
        AmazonMeta amazonMeta = null;
        if (html != null && html.contains("ISBN")) {
            Document document = Jsoup.parse(html);
            if (document != null) {
                amazonMeta = new AmazonMeta();
                Elements basicInformation = document.select("#detail_bullets_id").select("ul").select("li");
                for (Element BasicInformation : basicInformation) {
                    if (BasicInformation.select("b").text().equals("出版社:") && BasicInformation.text().contains(";")) {
                        amazonMeta.setPublisher(BasicInformation.text().replace("出版社:", "").split(";")[0].trim());
                        amazonMeta.setEditionOrder(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[0].trim());
                        amazonMeta.setIssuedDate(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[1].replace(")", "").trim());
                    } else if (BasicInformation.select("b").text().equals("出版社:") && !BasicInformation.text().contains(";")) {
                        amazonMeta.setPublisher(BasicInformation.text().replace("出版社:", "").split("\\(")[0].trim());
                        amazonMeta.setIssuedDate(BasicInformation.text().replace("出版社:", "").split("\\(")[1].replace(")", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("丛书名:")) {
                        amazonMeta.setSeries(BasicInformation.text().replace("丛书名:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("外文书名:")) {
                        amazonMeta.setOriginSeries(BasicInformation.text().replace("外文书名:", "").replace("'", "\\'").trim());
                    }
                    if (BasicInformation.select("b").text().equals("原书名:")) {
                        amazonMeta.setOriginTitle(BasicInformation.text().replace("原书名:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("精装:") || BasicInformation.select("b").text().equals("平装:")) {
                        if (BasicInformation.select("b").text().equals("精装:")) {
                            amazonMeta.setPages(BasicInformation.text().replace("精装:", "").trim());
                        }
                        if (BasicInformation.select("b").text().equals("平装:")) {
                            amazonMeta.setPages(BasicInformation.text().replace("平装:", "").trim());
                        }
                    }
                    if (BasicInformation.select("b").text().equals("语种：")) {
                        amazonMeta.setLanguage(BasicInformation.text().replace("语种：", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("开本:")) {
                        amazonMeta.setFormat(BasicInformation.text().replace("开本:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("ISBN:")) {
                        if (BasicInformation.text().replace("ISBN:", "").contains(",")) {
                            String ISBN = BasicInformation.text().replace("ISBN:", "");
                            if (ISBN.split(",")[1].trim().length() == 10) {
                                String isbn10 = ISBN.split(",")[1].trim();
                                if (isbn10.length() == 10) {
                                    amazonMeta.setIsbn10(isbn10.trim());
                                }
                                String isbn13 = ISBN.split(",")[0].trim();
                                if (isbn13.length() == 13) {
                                    amazonMeta.setIsbn13(isbn13.trim());
                                }
                            } else {
                                String isbn10 = ISBN.split(",")[0].trim();
                                if (isbn10.length() == 10) {
                                    amazonMeta.setIsbn10(isbn10.trim());
                                }
                                String isbn13 = ISBN.split(",")[1].trim();
                                if (isbn13.length() == 13) {
                                    amazonMeta.setIsbn13(isbn13.trim());
                                }
                            }
                        } else {
                            if (BasicInformation.text().replace("ISBN:", "").trim().length() == 10) {
                                String isbn10 = BasicInformation.text().replace("ISBN:", "").trim();
                                amazonMeta.setIsbn10(isbn10);
                            }
                            if (BasicInformation.text().replace("ISBN:", "").trim().length() == 13) {
                                String isbn13 = BasicInformation.text().replace("ISBN:", "").trim();
                                amazonMeta.setIsbn13(isbn13);
                            }
                        }
                    }
                    if (BasicInformation.select("b").text().equals("商品尺寸:")) {
                        amazonMeta.setProductSize(BasicInformation.text().replace("商品尺寸:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("商品重量:")) {
                        amazonMeta.setCommodityWeight(BasicInformation.text().replace("商品重量:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("品牌:")) {
                        amazonMeta.setBrand(BasicInformation.text().replace("品牌:", "").trim());
                    }
                    if (BasicInformation.select("b").text().equals("ASIN:")) {
                        amazonMeta.setAsin(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                        amazonMeta.setAmazonId(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                    }
                    if (BasicInformation.select("b").text().contains("亚马逊热销商品排名:")) {
                        amazonMeta.setClassification(BasicInformation.select("b").select("a").text().trim());
                    }
                }
                if (!document.select("div[id=rightCol]").text().contains("电子书定价:") || !document.select("div[id=rightCol]").text().contains("Kindle电子书价格:")) {
                    if (document.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").size() > 1) {
                        String price = document.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").get(1).text();
                        amazonMeta.setPaperPrice(price);
                    }
                }
                // TODO 修改空指针异常
                if (document.select("h1[class=a-size-large a-spacing-none]").select("span").attr("id").contains("ebooksProductTitle")) {
                    String title = document.select("h1[class=a-size-large a-spacing-none]").select("span[id=ebooksProductTitle]").first().text();
                    amazonMeta.setTitle(title.trim());
                } else {
                    String title = document.select("h1[class=a-size-large a-spacing-none]").select("span[id=productTitle]").first().text();
                    amazonMeta.setTitle(title.trim());
                }
                if (document.select("span[class=author notFaded]").select("a[class=a-link-normal]").first() != null) {
                    String author = document.select("span[class=author notFaded]").select("a[class=a-link-normal]").first().text();
                    amazonMeta.setAuthor(author.trim());
                }
                if (document.select("span[class=author notFaded]").select("a[class=a-link-normal]").size() > 1) {
                    String translator = document.select("span[class=author notFaded]").select("a[class=a-link-normal]").get(1).text();
                    amazonMeta.setTranslator(translator.trim());
                }
                if (document.select("span[class=a-size-small a-color-price]") != null) {
                    if (document.select("span[class=a-size-small a-color-price]").first() != null) {
                        if (document.select("span[class=a-size-small a-color-price]").first().text() != null) {
                            String kindlePrice = document.select("span[class=a-size-small a-color-price]").first().text();
                            amazonMeta.setKindlePrice(kindlePrice.trim());
                        }
                    }
                }
                if (document.select("h1[class=a-size-large a-spacing-none]").select("span").size() > 2) {
                    String binding = document.select("span[class=a-size-medium a-color-secondary a-text-normal]").first().text();
                    amazonMeta.setBinding(binding.trim());
                }
                String content = document.select("div[id=bookDescription_feature_div]").select("noscript").text().replace("海报：", "");
                amazonMeta.setSummary(content.trim());
                if (document.select("div[id=bookDescription_feature_div]").select("noscript").select("img") != null) {
                    String poster = document.select("div[id=bookDescription_feature_div]").select("noscript").select("img").attr("src");
                    amazonMeta.setPoster(poster.trim());
                }
                String merchantID = document.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=merchantID]").attr("value");
                String qid = document.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=qid]").attr("value");

                String detailurl = "https://www.amazon.cn/gp/product-description/ajaxGetProuductDescription.html?ref_=dp_apl_pc_loaddesc&asin=" + amazonMeta.getAsin() + "&merchantId=" + merchantID + "&deviceType=web";
                Document detaildoc = DomParseUtil.getDomByURL(detailurl);
                Elements details = detaildoc.select("div[class=a-section s-content]");

                for (Element detail : details) {
                    if (detail.select("h3").text().equals("编辑推荐")) {
                        amazonMeta.setEditRecommend(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("名人推荐")) {
                        amazonMeta.setCelebrityRecommend(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("媒体推荐")) {
                        amazonMeta.setMediaRecommendation(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("作者简介")) {
                        amazonMeta.setAuthorIntroduction(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("序言")) {
                        amazonMeta.setPreface(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("目录")) {
                        amazonMeta.setCatalog(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("文摘")) {
                        amazonMeta.setAbstract_(detail.select("p").text().replace("'", "\\'").trim());
                    }
                    if (detail.select("h3").text().equals("后记")) {
                        amazonMeta.setPostScript(detail.select("p").text().replace("'", "\\'").trim());
                    }
                }
            }
        }
        return amazonMeta;
    }

    public static void main(String[] args) {
//        AmazonMeta amazonMeta = crawlAmazonMetaById("B074BNFY1H", "119.82.253.182", "47364");
//        System.out.println(amazonMeta);
    }
}
