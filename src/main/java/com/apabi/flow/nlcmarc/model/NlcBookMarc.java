package com.apabi.flow.nlcmarc.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class NlcBookMarc {
    private String nlcMarcId;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private String metaId;

    private String isoFilePath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String isoContent;

    private String class_;

    private String titlePinyin;

    private String subTitle;

    private String subTitlePinyin;

    private String authorPinyin;

    private String contributor;

    private String issuedDate;

    private String relation;

    private String volume;

    private String volumeTitle;

    private String volumeTitlePinyin;

    private String volumeId;

    public NlcBookMarc() {
    }

    public String getTitlePinyin() {
        return titlePinyin;
    }

    public void setTitlePinyin(String titlePinyin) {
        this.titlePinyin = titlePinyin;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitlePinyin() {
        return subTitlePinyin;
    }

    public void setSubTitlePinyin(String subTitlePinyin) {
        this.subTitlePinyin = subTitlePinyin;
    }

    public String getAuthorPinyin() {
        return authorPinyin;
    }

    public void setAuthorPinyin(String authorPinyin) {
        this.authorPinyin = authorPinyin;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getVolumeTitlePinyin() {
        return volumeTitlePinyin;
    }

    public void setVolumeTitlePinyin(String volumeTitlePinyin) {
        this.volumeTitlePinyin = volumeTitlePinyin;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getNlcMarcId() {
        return nlcMarcId;
    }

    public void setNlcMarcId(String nlcMarcId) {
        this.nlcMarcId = nlcMarcId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getIsoFilePath() {
        return isoFilePath;
    }

    public void setIsoFilePath(String isoFilePath) {
        this.isoFilePath = isoFilePath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsoContent() {
        return isoContent;
    }

    public void setIsoContent(String isoContent) {
        this.isoContent = isoContent;
    }

    @Override
    public String toString() {
        return "NlcBookMarc{" +
                "nlcMarcId='" + nlcMarcId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                ", metaId='" + metaId + '\'' +
                ", isoFilePath='" + isoFilePath + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isoContent='" + isoContent + '\'' +
                ", class_='" + class_ + '\'' +
                ", titlePinyin='" + titlePinyin + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", subTitlePinyin='" + subTitlePinyin + '\'' +
                ", authorPinyin='" + authorPinyin + '\'' +
                ", contributor='" + contributor + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", relation='" + relation + '\'' +
                ", volume='" + volume + '\'' +
                ", volumeTitle='" + volumeTitle + '\'' +
                ", volumeTitlePinyin='" + volumeTitlePinyin + '\'' +
                ", volumeId='" + volumeId + '\'' +
                '}';
    }
}