package com.apabi.flow.nlcmarc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author pipi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApabiBookMetadataAuthor {
    private String id;

    private String metaId;

    private String authorType;

    private String name;

    private String type;

    private String pinyin;

    private String originalName;

    private String apabiAuthorId;

    private String nlcAuthorId;

    private String priority;

    private String nlcMarcIdentifier;

    private String addition;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String period;

    private String traname;

    private String traAuthorType;

}