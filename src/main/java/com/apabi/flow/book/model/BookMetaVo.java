package com.apabi.flow.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 16:57
 * @description
 */

public class BookMetaVo {

    private String metaId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createtime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updatetime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String publishDate;

    private String title;

    private String creator;

    private String summary;

    private String publisher;

    private String isbn;

    private String isbn10;

    private String isbn13;

    private Integer wordSum;

    private String isoptimize;

    private String language;

    private String coverUrl;

    private Integer chapterSum;

    private String cssUrl;

    private String coverMiniUrl;

    private String styleClass;

    private Integer hasflow;

    private Integer hascebx;

    private Integer isPublicCopyRight;

    private Integer saleStatus;

    private String flowSource;

    private Integer drid;

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getWordSum() {
        return wordSum;
    }

    public void setWordSum(Integer wordSum) {
        this.wordSum = wordSum;
    }

    public String getIsoptimize() {
        return isoptimize;
    }

    public void setIsoptimize(String isoptimize) {
        this.isoptimize = isoptimize;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getChapterSum() {
        return chapterSum;
    }

    public void setChapterSum(Integer chapterSum) {
        this.chapterSum = chapterSum;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    public void setCssUrl(String cssUrl) {
        this.cssUrl = cssUrl;
    }

    public String getCoverMiniUrl() {
        return coverMiniUrl;
    }

    public void setCoverMiniUrl(String coverMiniUrl) {
        this.coverMiniUrl = coverMiniUrl;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }


    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public Integer getHasflow() {
        return hasflow;
    }

    public void setHasflow(Integer hasflow) {
        this.hasflow = hasflow;
    }

    public Integer getHascebx() {
        return hascebx;
    }

    public void setHascebx(Integer hascebx) {
        this.hascebx = hascebx;
    }

    public Integer getIsPublicCopyRight() {
        return isPublicCopyRight;
    }

    public void setIsPublicCopyRight(Integer isPublicCopyRight) {
        this.isPublicCopyRight = isPublicCopyRight;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }

    public Integer getDrid() {
        return drid;
    }

    public void setDrid(Integer drid) {
        this.drid = drid;
    }
}
