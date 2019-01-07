package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author pipi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmazonMeta {
    private String amazonId;

    private String title;

    private String author;

    private String translator;

    private String isbn13;

    private String isbn10;

    private String publisher;

    private String originTitle;

    private String paperPrice;

    private String kindlePrice;

    private String editionOrder;

    private String issuedDate;

    private String originSeries;

    private String binding;

    private String pages;

    private String language;

    private String series;

    private String format;

    private String productSize;

    private String commodityWeight;

    private String brand;

    private String asin;

    private String classification;

    private String editRecommend;

    private String celebrityRecommend;

    private String mediaRecommendation;

    private String authorIntroduction;

    private String catalog;

    private String preface;

    private String abstract_;

    private String summary;

    private String poster;

    private String readerObject;

    private Integer hasCrawled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    private String postScript;
}