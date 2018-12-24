package com.apabi.flow.jd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author pipi
 * @Date 2018-12-04 11:56
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JdItemUrl {
    private String url;

    private String status;
}