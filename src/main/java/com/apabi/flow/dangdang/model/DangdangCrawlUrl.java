package com.apabi.flow.dangdang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-7 14:03
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangdangCrawlUrl {
    private String url;

    private String status;

    private String description;

    private String pageNum;
}
