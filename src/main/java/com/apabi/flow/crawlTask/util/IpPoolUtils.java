package com.apabi.flow.crawlTask.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pipi on 2018/7/24.
 */
@Order(1)
@Component
public class IpPoolUtils {
    private static Logger logger = LoggerFactory.getLogger(IpPoolUtils.class);
    public static List<String> ipPool = new ArrayList<>();
    private static int ipCount;

    static {
        logger.info("获取kuaidaili的ip列表开始...");
        String kuaidailiApiUrl = "http://svip.kdlapi.com/api/getproxy/?orderid=993991357970626&num=150&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sep=1";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(kuaidailiApiUrl);
        try {
            CloseableHttpResponse response = client.execute(get);
            String ipsContent = EntityUtils.toString(response.getEntity());
            String[] ips = ipsContent.split("\r\n");
            for (String ip : ips) {
                ipPool.add(ip);
            }
            ipCount = ipPool.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取kuaidaili的ip列表结束...");
    }

    private IpPoolUtils() {
    }

    public synchronized static String getIp() {
        Random random = new Random();
        int index = random.nextInt(ipCount);
        return ipPool.get(index);
    }
}
