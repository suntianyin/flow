package com.apabi.flow.config;

import com.apabi.flow.common.YmlPropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/11/20 14:05
 * @description
 */
@Component
@ConfigurationProperties
@PropertySource(value = "classpath:/properties/dic-word-data.yml", factory = YmlPropertyLoaderFactory.class)
public class DicWordData {

    //字典数据
    @Value("${words}")
    private String words;

    //字典数组
    private Map<String, String> dicUnicode;

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Map<String, String> getDicUnicode() {
        return dicUnicode;
    }

    public void setDicUnicode(Map<String, String> dicUnicode) {
        this.dicUnicode = dicUnicode;
    }
}
