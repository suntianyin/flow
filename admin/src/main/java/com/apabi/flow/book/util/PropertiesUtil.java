package com.apabi.flow.book.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;

/**
 * @author guanpp
 * @date 2018/8/2 13:33
 * @description
 */
public class PropertiesUtil {

    private static PropertiesConfiguration config = null;

    static {
        try {
            load();
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void load() throws ConfigurationException{
        config = new PropertiesConfiguration();
        config.setEncoding("UTF-8");
        config.load(PropertiesUtil.class.getClassLoader().getResource("properties/conf.properties").toString());
    }

    /**
     * 根据key得到value的值
     *
     * @throws IOException
     */
    public static String getValue(String key) throws IOException {
        return config.getString(key);
    }

    public static String get(final String key) {
        return config.getString(key);
    }
}
