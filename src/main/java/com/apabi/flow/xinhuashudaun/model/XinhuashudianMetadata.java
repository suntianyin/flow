package com.apabi.flow.xinhuashudaun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018-12-14 14:16
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XinhuashudianMetadata {
    private String itemId;

    private String title;

    private String author;

    private String isbn;

    private String publisher;

    private String issuedDate;

    private String binding;

    private String pages;

    private String language;

    private String format;

    private String printTime;

    private String metaId;

    private String cip;

    private String coverImgUrl;

    private Date createTime;

    private Date updateTime;

    private String editionOrder;
}
