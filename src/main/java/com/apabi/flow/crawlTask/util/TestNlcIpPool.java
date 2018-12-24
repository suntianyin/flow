package com.apabi.flow.crawlTask.util;

/**
 * @Author pipi
 * @Date 2018-12-3 14:03
 **/
public class TestNlcIpPool {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            nlcIpPoolUtils.getIp();
        }
    }
}
