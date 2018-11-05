package com.apabi.flow.newspaper.cnr.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author pipi
 * @date 2018/7/24
 */
public class CnrIpPoolUtils {
    private static Logger logger = LoggerFactory.getLogger(CnrIpPoolUtils.class);
    private static HashSet<String> ipSet = new HashSet<String>();
    private List<String> ipPool = new ArrayList<>();
    private static int ipCount;
    private static final int PROXY_VISIT_COUNT = 3;
    // 提取数量：200
    // 高匿，快速，稳定，加密
    private static final String API_URL = "http://svip.kdlapi.com/api/getproxy/?orderid=993991357970626&num=500&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sp1=1&sp2=1&quality=1&sep=1";

    public CnrIpPoolUtils() {
        logger.info("获取kuaidaili的ip列表开始...");
        CloseableHttpClient client = HttpClients.createDefault();
        for (int i = 0; i < PROXY_VISIT_COUNT; i++) {
            HttpGet get = new HttpGet(API_URL);
            try {
                CloseableHttpResponse response = client.execute(get);
                String ipsContent = EntityUtils.toString(response.getEntity());
                String[] ips = ipsContent.split("\r\n");
                for (String ip : ips) {
                    boolean add = ipSet.add(ip);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ipCount = ipSet.size();
        for (String ip : ipSet) {
            ipPool.add(ip);
        }
        logger.info("获取kuaidaili的ip列表结束...ip列表大小为：" + ipPool.size());
    }

    public String getIp() {
        Random random = new Random();
        int index = random.nextInt(ipCount)-1;
        if(index<0){
            index = 0;
        }
        String ip = ipPool.get(index);
        return ip;
    }
}
