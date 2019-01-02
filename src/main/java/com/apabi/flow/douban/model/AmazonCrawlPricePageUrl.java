package com.apabi.flow.douban.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-24 16:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmazonCrawlPricePageUrl {
    private String url;

    private String status;
}
