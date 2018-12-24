package com.apabi.flow.isbn_douban_amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2018-12-12 10:51
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsbnDoubanAmazon {
    private String isbn;

    private String doubanStatus;

    private String amazonStatus;
}
