package com.apabi.flow.crawlTask.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author pipi
 * @date 2018/7/24
 */
public class NlcIpPoolUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(NlcIpPoolUtils.class);
    private List<String> ipPool;
    private int ipCount;
    private static final int PROXY_VISIT_COUNT = 3;
    /**
     * 提取数量：900
     * 高匿，快速，稳定，加密
     */
    private static final String API_URL = "http://svip.kdlapi.com/api/getproxy/?orderid=993991357970626&num=900&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=1&an_an=1&an_ha=1&sp1=1&sp2=1&sort=2&sep=1";

    public NlcIpPoolUtils() {
        LOGGER.info("获取kuaidaili的ip列表开始...");
        ipPool = new ArrayList<>();
        for (int i = 0; i < PROXY_VISIT_COUNT; i++) {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(API_URL);
            try {
                CloseableHttpResponse response = client.execute(get);
                String ipsContent = EntityUtils.toString(response.getEntity());
                String[] ips = ipsContent.split("\r\n");
                for (String ip : ips) {
                    ipPool.add(ip);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // 休息3秒后再次请求kuaidaili的api，生成较多的ip
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ipCount = ipPool.size();
        LOGGER.info("获取kuaidaili的ip列表结束...ip列表大小为：" + ipPool.size());
    }

    public String getIp() {
        Random random = new Random();
        int index = random.nextInt(ipCount);
        String ip = ipPool.get(index);
        return ip;
    }
}
