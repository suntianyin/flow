package com.apabi.flow.config;

import com.apabi.flow.common.YmlPropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author guanpp
 * @date 2018/11/20 14:05
 * @description
 */
@Component
@PropertySource(value = "classpath:/properties/dic-word-data.yml", factory = YmlPropertyLoaderFactory.class)
public class DicWordData {

    //字典数据
    @Value("${words}")
    private String words;

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
