package com.apabi.flow.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author guanpp
 * @date 2018/7/31 17:45
 * @description
 */
@Configuration
public class DruidConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

//    @Bean
//    @ConfigurationProperties(prefix = "spring.data.mongodb")
//    public DataSource druidSecDataSource() {
//        DruidDataSource druidSecDataSource = new DruidDataSource();
//        return druidSecDataSource;
//    }
}
