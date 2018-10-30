package com.apabi.flow.cleanData.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author pipi
 * @Date 2018/10/30 10:01
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NlcCrawlIsbn {
    private String isbn;
}
