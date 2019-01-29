package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author pipi
 * @date 2018/10/30 14:12
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApabiBookMetaDataTemp {
    private String metaId;

    private String idType;

    private Integer saleStatus;

    private String language;

    private String title;

    private String subTitle;

    private String creator;

    private String creatorWord;

    private String creatorId;

    private String contributor;

    private String contributorWord;

    private String contributorId;

    private String translator;

    private String translatorId;

    private String originTitle;

    private String alternativeTitle;

    private String editionOrder;

    private String editionNote;

    private String place;

    private String publisher;

    private String publisherId;

    private String issuedDate;

    private String endIssuedDate;

    private String issuedDateDesc;

    private String subject;

    private String reader;

    private String classCode;

    private String apabiClass;

    private String type;

    private String isbn;

    private String isbn10;

    private String isbn13;

    private String paperPrice;

    private String ebookPrice;

    private String foreignPrice;

    private String foreignPriceType;

    private String paperPriceDesc;

    private String binding;

    private String illustration;

    private String pressOrder;

    private String editor;

    private String relation;

    private String relationId;

    private String volume;

    private Integer isSeries;

    private String volumesCount;

    private String isAllublished;

    private String notes;

    private String podPrice;

    private String podPriceType;

    private String podPricePageUnit;

    private Integer drId;

    private String doubanId;

    private String amazonId;

    private String calisId;

    private String nlibraryId;

    private String dataSource;

    private String qualityRating;

    private String qrType;

    private String cebxObjId;

    private String cebxFileSize;

    private Integer hasCebx;

    private String cebxPage;

    private String complexOid;

    private String reditor;

    private String departmentId;

    private String coverUrl;

    private String imgHeigth;

    private String imgWidth;

    private String covObjId;

    private String thumImgUrl;

    private String thumImgSize;

    private String mediumCover;

    private Integer hasFlow;

    private Integer isOptimize;

    private String donor;

    private String libraryId;

    private String bookId;

    private String styleClass;

    private String styleUrl;

    private Integer chapterNum;

    private String bookPages;

    private String tags;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer contentNum;

    private Integer hasPublish;

    private Integer isPublicCopyRight;

    private String relationType;

    private String authorIntro;

    private String abstract_;

    private String preface;

    private String foamatCatalog;

    private String streamCatalog;

    private String postScript;

    private Integer isReadEpub;

    private String flowSource;

    private Integer isReadCebxFlow;

    private Integer hasProcessingFile;

    private Integer hasClean;
}