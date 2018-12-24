package com.apabi.flow.jd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-4 17:22
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JdCrawlPageUrl {
    private String url;
    private String status;
}
