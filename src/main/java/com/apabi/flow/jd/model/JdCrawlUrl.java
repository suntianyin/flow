package com.apabi.flow.jd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author pipi
 * @Date 2018-12-04 11:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JdCrawlUrl {
    private String url;

    private String status;

    private String description;

    private String pageNum;
}