package com.apabi.flow.isbn_douban_amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-1-18 10:42
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsbnDangdang {
    private String isbn;

    private String doubanStatus;

    private String amazonStatus;

    private String nlcStatus;
}
