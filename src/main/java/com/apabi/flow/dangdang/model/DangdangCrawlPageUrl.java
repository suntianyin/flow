package com.apabi.flow.dangdang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-7 14:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DangdangCrawlPageUrl {
    private String url;

    private String status;
}
