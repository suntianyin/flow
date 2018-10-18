package com.apabi.flow.nlcmarc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pipi on 2018/7/24.
 */
public class IpPoolUtils {
    public  static volatile List<String> ipPool = new ArrayList<>();
    private static int ipCount;

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(IpPoolUtils.class.getClassLoader().getResourceAsStream("properties/ip.properties")));
            String str = null;
            while ((str = reader.readLine()) != null) {
                ipPool.add(str);
            }
            ipCount = ipPool.size();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private IpPoolUtils() {
    }


    public synchronized static String getIp() {
        Random random = new Random();
        int index = random.nextInt(ipCount);
        return ipPool.get(index);
    }

}
