package com.apabi.flow.xinhuashudaun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-14 14:56
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XinhuashudianCrawlUrl {
    private String url;

    private String status;

    private String description;

    private String pageNum;
}
