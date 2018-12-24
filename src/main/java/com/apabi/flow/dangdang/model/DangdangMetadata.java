package com.apabi.flow.dangdang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018-12-7 9:18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DangdangMetadata {
    private String pid;

    private String title;

    private String isbn13;

    private String isbn10;

    private String classification;

    private Date createTime;

    private Date updateTime;
}
