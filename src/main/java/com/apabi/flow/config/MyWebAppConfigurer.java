package com.apabi.flow.config;

import com.apabi.flow.common.UniversalEnumHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 功能描述： <br>
 * <web-mvc 配置>
 *
 * @author supeng
 * @date 2018/9/5 18:06
 * @since 1.0.0
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    /**
     * 配置枚举解析器
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new UniversalEnumHandler());
    }
}