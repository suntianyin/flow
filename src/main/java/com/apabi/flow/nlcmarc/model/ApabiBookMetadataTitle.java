package com.apabi.flow.nlcmarc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/10/10 11:03
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApabiBookMetadataTitle {
    private String id;

    private String metaId;

    private String nlcMarcIdentifier;

    private String title;

    private String subTitle;

    private String titlePinyin;

    private String seriesTitle;

    private String parallelSeriesTitle;

    private String seriesSubTitle;

    private String volumeTitle;

    private String parallelTitle;

    private String coverTitle;

    private String addedPageTitle;

    private String captionTitle;

    private String runningTitle;

    private String spineTitle;

    private String otherVariantTitle;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String volume200;

    private String volume200Title;

    private String volume;

    private String volume500;

    private String volume500Title;

    private String uniformTitle;
}
