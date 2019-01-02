package com.apabi.flow.douban.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-24 15:49
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmazonCrawlPriceUrl {
    private String url;

    private String pageNum;

    private String status;
}
