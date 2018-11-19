package com.apabi.flow.newspaper.crawl_url.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author pipi
 * @Date 2018/11/7 9:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors
public class NewspaperCrawlUrl {
    private String indexPage;

    private String columnName;

    private String source;

    private int pageNum;

}
