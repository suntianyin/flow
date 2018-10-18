package com.apabi.flow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;

/**
 * @author guanpp
 * @date 2018/8/10 9:19
 * @description
 */
@Configuration
public class FreeMarkerConfig {
    @Autowired
    private freemarker.template.Configuration configuration;

    private InternalResourceViewResolver resourceViewResolver;

    @Value("${ctx}")
    private String ctx;

    // Spring 初始化的时候加载配置
    @PostConstruct
    public void setConfigure() throws Exception {
        // 加载html的资源路径
        configuration.setSharedVariable("ctx", ctx);

    }
}
