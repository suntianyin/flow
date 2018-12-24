package com.apabi.flow.dangdang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-19 17:12
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangdangCrawlPriceUrl {
    private String url;
    private String status;
    private String pageNum;
}
