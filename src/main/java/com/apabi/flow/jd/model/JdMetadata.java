package com.apabi.flow.jd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018-12-04 10:56
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JdMetadata {
    private String jdItemId;

    private String title;

    private String isbn13;

    private String publisher;

    private String editionOrder;

    private String issuedDate;

    private String binding;

    private Integer pages;

    private String language;

    private String format;

    private String brand;

    private String metaId;

    private Date createTime;

    private Date updateTime;
}
