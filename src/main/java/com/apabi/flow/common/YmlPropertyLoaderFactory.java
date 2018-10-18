package com.apabi.flow.common;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * 功能描述： <br>
 * <自定义 yml 资源加载>
 *
 * @author supeng
 * @date 2018/8/21 15:59
 * @since 1.0.0
 */
public class YmlPropertyLoaderFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        if (resource == null){
            super.createPropertySource(name, resource);
        }
        return new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource()).get(0);
    }
}