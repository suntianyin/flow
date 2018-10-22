package com.apabi.flow.douban.util;

import com.apabi.flow.douban.model.AmazonMeta;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
 * @Date 2018/10/17 11:09
 **/
public class CrawlAmazonUtils {
    private static Logger logger = LoggerFactory.getLogger(CrawlAmazonUtils.class);

    private static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
        return client;
    }

    // 从douban根据抓取列表获取该页面中的douban数据
    public static List<String> crawlAmazonIdList(String url, String ip, String port) throws IOException {
        List<String> idList = new ArrayList<>();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问amazon分类首页
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "x-wl-uid=1qZrbvFORWrlBt7g3av6suH/mZemuPPt6VL/xnnn+i+Xcz1vZMfd4hpI1A3wzCLNV38U1B2V3lzk=; p2ePopoverID_461-8070897-5914413=1; csm-hit=tb:s-GAFPTTH7STCZ6DNW6GBR|1539745906148&adb:adblk_no; session-id=461-8070897-5914413; session-id-time=2082787201l; ubid-acbcn=462-3409704-4738518; session-token=\"xkIYwJerzkW+z7FWSnKti1TKVPIu3x254h9qeyS7Z1yw5Q8pN6Ed+8wWESh5XpkWSGeWDbdSmi20DMGam5JyuJCqaZ1vzvGFlBYEEcwKESPywSK8xnP/PoDO4lUqRtr84AzqE+Qu8UgP/tfVhqfkAi7b0wro5l5a3osrxeeKnVXXiC4toTUyEawpNozRH7sofCt6mm1VcK5pSufHDTxP7SB2ANtgalZWFHpF73sz4ydAmYISe84IKw==\"");
        httpGet.setHeader("Host", "www.amazon.cn");
        httpGet.setHeader("Upgrade-Insecure-Request", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        CloseableHttpResponse response1 = null;
        try {
            response1 = client.execute(httpGet);
            String html = EntityUtils.toString(response1.getEntity());
            Document document = Jsoup.parse(html);
            Elements elements = document.select("li[class='s-result-item celwidget  ']");
            for (Element element : elements) {
                String id = element.attr("data-asin");
                idList.add(id);
            }
        } catch (IOException e) {
            logger.error("线程" + Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上amazon主页");
            e.printStackTrace();
            httpGet.releaseConnection();
            httpGet.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new IOException();
        }
        return idList;
    }

    public static AmazonMeta crawlAmazonMetaById(String id, String ip, String port) throws IOException {
        AmazonMeta amazonMeta = new AmazonMeta();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        String url = "https://www.amazon.cn/dp/" + id + "/ref=sr_1_9?s=books&ie=UTF8&qid=1539756341&sr=1-9";
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "x-wl-uid=1qZrbvFORWrlBt7g3av6suH/mZemuPPt6VL/xnnn+i+Xcz1vZMfd4hpI1A3wzCLNV38U1B2V3lzk=; p2ePopoverID_461-8070897-5914413=1; csm-hit=tb:s-GAFPTTH7STCZ6DNW6GBR|1539745906148&adb:adblk_no; session-id=461-8070897-5914413; session-id-time=2082787201l; ubid-acbcn=462-3409704-4738518; session-token=\"xkIYwJerzkW+z7FWSnKti1TKVPIu3x254h9qeyS7Z1yw5Q8pN6Ed+8wWESh5XpkWSGeWDbdSmi20DMGam5JyuJCqaZ1vzvGFlBYEEcwKESPywSK8xnP/PoDO4lUqRtr84AzqE+Qu8UgP/tfVhqfkAi7b0wro5l5a3osrxeeKnVXXiC4toTUyEawpNozRH7sofCt6mm1VcK5pSufHDTxP7SB2ANtgalZWFHpF73sz4ydAmYISe84IKw==\"");
        httpGet.setHeader("Host", "www.amazon.cn");
        httpGet.setHeader("Upgrade-Insecure-Request", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            if (document != null) {
                Elements basicInformation = document.select("#detail_bullets_id").select("ul").select("li");
                for (Element BasicInformation : basicInformation) {
                    if (BasicInformation.select("b").text().equals("出版社:") && BasicInformation.text().contains(";")) {
                        amazonMeta.setPublisher(BasicInformation.text().replace("出版社:", "").split(";")[0]);
                        amazonMeta.setEditionOrder(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[0]);
                        amazonMeta.setIssuedDate(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[1].replace(")", ""));
                    } else if (BasicInformation.select("b").text().equals("出版社:") && !BasicInformation.text().contains(";")) {
                        amazonMeta.setPublisher(BasicInformation.text().replace("出版社:", "").split("\\(")[0]);
                        amazonMeta.setIssuedDate(BasicInformation.text().replace("出版社:", "").split("\\(")[1].replace(")", ""));
                    }
                    if (BasicInformation.select("b").text().equals("丛书名:")) {
                        amazonMeta.setSeries(BasicInformation.text().replace("丛书名:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("外文书名:")) {
                        amazonMeta.setOriginSeries(BasicInformation.text().replace("外文书名:", "").replace("'", "\\'"));
                    }
                    if (BasicInformation.select("b").text().equals("原书名:")) {
                        amazonMeta.setOriginTitle(BasicInformation.text().replace("原书名:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("精装:") || BasicInformation.select("b").text().equals("平装:")) {
                        if (BasicInformation.select("b").text().equals("精装:")) {
                            amazonMeta.setPages(BasicInformation.text().replace("精装:", ""));
                        }
                        if (BasicInformation.select("b").text().equals("平装:")) {
                            amazonMeta.setPages(BasicInformation.text().replace("平装:", ""));
                        }
                    }
                    if (BasicInformation.select("b").text().equals("语种：")) {
                        amazonMeta.setLanguage(BasicInformation.text().replace("语种：", ""));
                    }
                    if (BasicInformation.select("b").text().equals("开本:")) {
                        amazonMeta.setFormat(BasicInformation.text().replace("开本:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("ISBN:")) {
                        if (BasicInformation.text().replace("ISBN:", "").contains(",")) {
                            String ISBN = BasicInformation.text().replace("ISBN:", "");
                            if (ISBN.split(",")[1].trim().length() == 10) {
                                String isbn10 = ISBN.split(",")[1].trim();
                                if (isbn10.length() == 10) {
                                    amazonMeta.setIsbn10(isbn10);
                                }
                                String isbn13 = ISBN.split(",")[0].trim();
                                if (isbn13.length() == 13) {
                                    amazonMeta.setIsbn13(isbn13);
                                }
                            } else {
                                String isbn10 = ISBN.split(",")[0].trim();
                                if (isbn10.length() == 10) {
                                    amazonMeta.setIsbn10(isbn10);
                                }
                                String isbn13 = ISBN.split(",")[1].trim();
                                if (isbn13.length() == 13) {
                                    amazonMeta.setIsbn13(isbn13);
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
                        amazonMeta.setProductSize(BasicInformation.text().replace("商品尺寸:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("商品重量:")) {
                        amazonMeta.setCommodityWeight(BasicInformation.text().replace("商品重量:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("品牌:")) {
                        amazonMeta.setBrand(BasicInformation.text().replace("品牌:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("ASIN:")) {
                        amazonMeta.setAsin(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                        amazonMeta.setAmazonId(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                    }
                    if (BasicInformation.select("b").text().contains("亚马逊热销商品排名:")) {
                        amazonMeta.setClassification(BasicInformation.select("b").select("a").text());
                    }
                }
                if (!document.select("div[id=rightCol]").text().contains("电子书定价:") || !document.select("div[id=rightCol]").text().contains("Kindle电子书价格:")) {
                    if (document.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").size() > 1) {
                        String price = document.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").get(1).text();
                        amazonMeta.setPaperPrice(price);
                    }
                }
                if (document.select("h1[class=a-size-large a-spacing-none]").select("span").attr("id").contains("ebooksProductTitle")) {
                    String title = document.select("h1[class=a-size-large a-spacing-none]").select("span[id=ebooksProductTitle]").first().text();
                    amazonMeta.setTitle(title);
                } else {
                    String title = document.select("h1[class=a-size-large a-spacing-none]").select("span[id=productTitle]").first().text();
                    amazonMeta.setTitle(title);
                }
                if (document.select("span[class=author notFaded]").select("a[class=a-link-normal]").first() != null) {
                    String author = document.select("span[class=author notFaded]").select("a[class=a-link-normal]").first().text();
                    amazonMeta.setAuthor(author);
                }
                if (document.select("span[class=author notFaded]").select("a[class=a-link-normal]").size() > 1) {
                    String translator = document.select("span[class=author notFaded]").select("a[class=a-link-normal]").get(1).text();
                    amazonMeta.setTranslator(translator);
                }
                if (document.select("span[class=a-size-small a-color-price]") != null) {
                    if (document.select("span[class=a-size-small a-color-price]").first() != null) {
                        if (document.select("span[class=a-size-small a-color-price]").first().text() != null) {
                            String kindlePrice = document.select("span[class=a-size-small a-color-price]").first().text();
                            amazonMeta.setKindlePrice(kindlePrice);
                        }
                    }
                }
                if (document.select("h1[class=a-size-large a-spacing-none]").select("span").size() > 2) {
                    String binding = document.select("span[class=a-size-medium a-color-secondary a-text-normal]").first().text();
                    amazonMeta.setBinding(binding);
                }
                String content = document.select("div[id=bookDescription_feature_div]").select("noscript").text().replace("海报：", "");
                amazonMeta.setSummary(content);
                if (document.select("div[id=bookDescription_feature_div]").select("noscript").select("img") != null) {
                    String poster = document.select("div[id=bookDescription_feature_div]").select("noscript").select("img").attr("src");
                    amazonMeta.setPoster(poster);
                }
                String merchantID = document.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=merchantID]").attr("value");
                String qid = document.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=qid]").attr("value");

                String detailurl = "https://www.amazon.cn/gp/product-description/ajaxGetProuductDescription.html?ref_=dp_apl_pc_loaddesc&asin=" + amazonMeta.getAsin() + "&merchantId=" + merchantID + "&deviceType=web";
                Document detaildoc = DomParseUtil.getDomByURL(detailurl);
                Elements details = detaildoc.select("div[class=a-section s-content]");

                for (Element detail : details) {
                    if (detail.select("h3").text().equals("编辑推荐")) {
                        amazonMeta.setEditRecommend(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("名人推荐")) {
                        amazonMeta.setCelebrityRecommend(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("媒体推荐")) {
                        amazonMeta.setMediaRecommendation(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("作者简介")) {
                        amazonMeta.setAuthorIntroduction(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("序言")) {
                        amazonMeta.setPreface(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("目录")) {
                        amazonMeta.setCatalog(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("文摘")) {
                        amazonMeta.setAbstract_(detail.select("p").text().replace("'", "\\'"));
                    }
                    if (detail.select("h3").text().equals("后记")) {
                        amazonMeta.setPostScript(detail.select("p").text().replace("'", "\\'"));
                    }
                }
            }
        } catch (IOException e) {
            logger.error("线程" + Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上amazon详情页");
            e.printStackTrace();
            httpGet.releaseConnection();
            httpGet.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new IOException();
        }
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
        return amazonMeta;
    }
}
