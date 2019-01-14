package com.apabi.flow.processing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempMetaData {

    private String metaId;

    private String idType;

    private String saleStatus;

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

    private String isSeries;

    private String volumesCount;

    private String isAllublished;

    private String notes;

    private String podPrice;

    private String podPriceType;

    private String podPricePageUnit;

    private String drId;

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

    private String chapterNum;

    private String bookPages;

    private String tags;

    private String createTime;

    private String updateTime;

    private String contentNum;

    private String foamatCatalog;

    private List<StreamCatalog> streamCatalog;

    private String hasPublish;

    private String isPublicCopyRight;

    private String relationType;

    private String authorIntro;

    private String abstract_;

    private String preface;

    private String styleUrl;

    private String postScript;

    private String isReadEpub;

    private String flowSource;

    private String isReadCebxFlow;

    private String hasProcessingFile;

    class StreamCatalog{
        private String chapterName;
        private String chapterNum;
        private String children;
        private String ebookPageNum;
        private String wordSum;
        private String url;

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getChapterNum() {
            return chapterNum;
        }

        public void setChapterNum(String chapterNum) {
            this.chapterNum = chapterNum;
        }

        public String getChildren() {
            return children;
        }

        public void setChildren(String children) {
            this.children = children;
        }

        public String getEbookPageNum() {
            return ebookPageNum;
        }

        public void setEbookPageNum(String ebookPageNum) {
            this.ebookPageNum = ebookPageNum;
        }

        public String getWordSum() {
            return wordSum;
        }

        public void setWordSum(String wordSum) {
            this.wordSum = wordSum;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}