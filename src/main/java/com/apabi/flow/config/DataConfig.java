package com.apabi.flow.config;

import com.apabi.flow.common.YmlPropertyLoaderFactory;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能描述： <br>
 * <需要配置一些数据用于操作，比如 metaid 等>
 *
 * @author supeng
 * @date 2018/8/21 9:28
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "flow", ignoreInvalidFields = true)
@PropertySource(value = "classpath:/properties/data-config.yml", factory = YmlPropertyLoaderFactory.class)
public class DataConfig {

    private List<String> pageMetaIdList;

    private List<String> chapterMetaIdList;

    public List<String> getPageMetaIdList() {
        return pageMetaIdList;
    }

    private List<String> tempMetaIdList;

    public List<String> getTempMetaIdList() {
        return tempMetaIdList;
    }

    public void setTempMetaIdList(List<String> tempMetaIdList) {
        this.tempMetaIdList = tempMetaIdList;
    }

    public void setPageMetaIdList(List<String> pageMetaIdList) {
        this.pageMetaIdList = pageMetaIdList;
    }

    public List<String> getChapterMetaIdList() {
        return chapterMetaIdList;
    }

    public void setChapterMetaIdList(List<String> chapterMetaIdList) {
        this.chapterMetaIdList = chapterMetaIdList;
    }
}
