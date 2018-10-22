package com.apabi.flow.nlcmarc.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

public class CrawlNlcMarcUtil {
    private static Logger logger = LoggerFactory.getLogger(CrawlNlcMarcUtil.class);
    private static Random random = new Random();

    public static CloseableHttpClient getCloseableHttpClient(String ip, String port) {
        // 设置代理IP、端口、协议
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
        // CloseableHttpClient client = HttpClients.createDefault();
        return client;
    }

    // 从国图根据isbn或者isbn13获取marc数据内容
    public static String crawlNlcMarc(String ISBN, String ip, String port) {
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = getCloseableHttpClient(ip, port);
        // 访问国图首页
        HttpGet httpGet1 = new HttpGet("http://opac.nlc.cn/F/");
        httpGet1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet1.setHeader("Connection", "keep-alive");
        CloseableHttpResponse response1 = null;
        // 访问国图首页，获取标识码
        try {
            response1 = client.execute(httpGet1);
        } catch (IOException e) {
            logger.error("线程" + Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接不上国图主页");
            e.printStackTrace();
            httpGet1.releaseConnection();
            httpGet1.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        // 防封IP
        try {
            Thread.sleep(random.nextInt(2000) + 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取response1的实体类
        HttpEntity entity1 = response1.getEntity();
        String html = null;
        try {
            html = EntityUtils.toString(entity1, "GBK");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            EntityUtils.consumeQuietly(entity1);
            // 关闭response1
            try {
                response1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 利用Jsoup解析html
        Document document = Jsoup.parse(html);
        String string = document.select("form[name='form1']").attr("action");
        String first = "http://opac.nlc.cn/F/";
        String second = string.substring(string.lastIndexOf("/") + 1, string.length());
        String third = "?func=find-b&find_code=ISB&request=" + ISBN + "&local_base=NLC01&filter_code_1=WLN&filter_request_1=&filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFM&filter_request_4=&filter_code_5=WSL&filter_request_5=";
        // 通过ISBN查询
        String firstURL = first + second + third;
        httpGet1.releaseConnection();
        HttpGet httpGet2 = new HttpGet(firstURL);
        httpGet2.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet2.setHeader("Connection", "keep-alive");
        // 防封IP
        try {
            Thread.sleep(random.nextInt(2000) + 350);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response2 = null;
        try {
            response2 = client.execute(httpGet2);
        } catch (IOException e) {
            logger.error(Thread.currentThread().getName() + "使用" + ip + ":" + port + "连接失败！！！" + firstURL + "访问不到，" + ISBN + "无法进行查询");
            e.printStackTrace();
            httpGet2.releaseConnection();
            httpGet2.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        HttpEntity entity2 = response2.getEntity();
        String html2 = null;
        try {
            html2 = EntityUtils.toString(entity2, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            EntityUtils.consumeQuietly(entity2);
            try {
                EntityUtils.consume(entity2);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            logger.error(ISBN + "下载出错！");
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        String first1 = first;
        String second1 = second;
        String third1 = "?func=full-mail&doc_library=NLC01&doc_number=" + doc_number + "&option_type=&format=997&encoding=NONE&SUBJECT=&NAME=&EMAIL=&text=&x=90&y=9";
        String finalURL2 = first1 + second1 + third1;
        httpGet2.releaseConnection();
        HttpGet httpGet3 = new HttpGet(finalURL2);
        httpGet3.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        httpGet3.setHeader("Connection", "keep-alive");
        // 防封ip
        try {
            Thread.sleep(random.nextInt(1500) + 250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response3 = null;
        try {
            response3 = client.execute(httpGet3);
        } catch (IOException e) {
            logger.error(Thread.currentThread().getName() + "使用" + ip + ":" + port + "访问不到：" + finalURL2);
            e.printStackTrace();
            httpGet3.releaseConnection();
            httpGet3.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        HttpEntity entity3 = response3.getEntity();
        String html3 = null;
        try {
            html3 = EntityUtils.toString(entity3, "UTF-8");
        } catch (IOException e) {
            EntityUtils.consumeQuietly(entity3);
            try {
                EntityUtils.consume(entity3);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
        // 防封ip
        try {
            Thread.sleep(random.nextInt(1500) + 400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response4 = null;
        try {
            response4 = client.execute(httpGet4);
        } catch (IOException e) {
            logger.error("线程" + Thread.currentThread().getName() + "使用" + ip + ":" + port + "访问不到：" + href1 + "，无法对" + ISBN + "进行保存");
            e.printStackTrace();
            httpGet4.releaseConnection();
            httpGet4.abort();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        HttpEntity entity4 = response4.getEntity();
        String iso = null;
        try {
            iso = EntityUtils.toString(entity4, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            EntityUtils.consumeQuietly(entity4);
            try {
                EntityUtils.consume(entity4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭response4
            try {
                response4.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iso;
    }

    public static void main(String[] args) {
        String s = crawlNlcMarc("9787513631525", "106.75.164.15", "3128");
        System.out.println(s);
    }
}
