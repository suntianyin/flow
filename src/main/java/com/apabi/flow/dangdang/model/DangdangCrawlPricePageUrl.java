package com.apabi.flow.dangdang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-21 11:07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangdangCrawlPricePageUrl {
    private String url;
    private String status;
}
