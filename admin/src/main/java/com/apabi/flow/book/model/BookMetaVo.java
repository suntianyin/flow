package com.apabi.flow.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 16:57
 * @description
 */
@Table(name = "APABI_BOOK_METADATA")
@Entity
public class BookMetaVo {
    @Id
    @Column(name = "METAID")
    private String metaid;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "CREATETIME")
    private Date createtime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "UPDATETIME")
    private Date updatetime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "ISSUEDDATE")
    //private String issueddate;
    private String publishDate;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "ABSTRACT")
    private String summary;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "ISBN10")
    private String isbn10;

    @Column(name = "ISBN13")
    private String isbn13;

    @Column(name = "CONTENTNUM")
    //private Integer contentNum;
    private Integer wordSum;

    @Column(name = "ISOPTIMIZE")
    private String isoptimize;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "COVERURL")
    private String coverUrl;

    @Column(name = "CHAPTERNUM")
    private Integer chapterSum;

    @Column(name = "STYLEURL")
    private String cssUrl;

    @Column(name = "THUMIMGURL")
    //private String thumimgUrl;
    private String coverMiniUrl;

    @Column(name = "STYLECLASS")
    private String styleClass;

    @Column(name = "HASFLOW")
    private Integer hasflow;

    @Column(name = "HASCEBX")
    private Integer hascebx;

    public String getMetaid() {
        return metaid;
    }

    public void setMetaid(String metaid) {
        this.metaid = metaid;
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
}
