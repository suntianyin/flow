package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pipi
 * @date 2018/8/8 15:12
 * @description
 */
public class DoubanMeta implements Serializable {

    private String doubanId;

    private String metaId;

    private String isbn10;

    private String isbn13;

    private String title;

    private String originTitle;

    private String altTitle;

    private String subTitle;

    private String author;

    private String publisher;

    private String translator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String issueddate;

    private String pages;

    private String price;

    private String ebookPrice;

    private String binding;

    private String series;

    private String average;

    private String summary;

    private String authorIntro;

    private String catalog;

    private String tags;

    private String smallCover;

    private String mediumCover;

    private String largeCover;

    private Integer hasPublish;

    private String hasCrawled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public Integer getHasPublish() {
        return hasPublish;
    }

    public void setHasPublish(Integer hasPublish) {
        this.hasPublish = hasPublish;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
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

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getIssueddate() {
        return issueddate;
    }

    public void setIssueddate(String issueddate) {
        this.issueddate = issueddate;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getHasCrawled() {
        return hasCrawled;
    }

    public void setHasCrawled(String hasCrawled) {
        this.hasCrawled = hasCrawled;
    }

    public String getSubtitle() {
        return subTitle;
    }

    public void setSubtitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getEbookPrice() {
        return ebookPrice;
    }

    public void setEbookPrice(String ebookPrice) {
        this.ebookPrice = ebookPrice;
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

    public String getSmallCover() {
        return smallCover;
    }

    public void setSmallCover(String smallCover) {
        this.smallCover = smallCover;
    }

    public String getMediumCover() {
        return mediumCover;
    }

    public void setMediumCover(String mediumCover) {
        this.mediumCover = mediumCover;
    }

    public String getLargeCover() {
        return largeCover;
    }

    public void setLargeCover(String largeCover) {
        this.largeCover = largeCover;
    }

    @Override
    public String toString() {
        return "DoubanMeta{" +
                "doubanId='" + doubanId + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", altTitle='" + altTitle + '\'' +
                ", translator='" + translator + '\'' +
                ", issueddate='" + issueddate + '\'' +
                ", pages='" + pages + '\'' +
                ", price='" + price + '\'' +
                ", binding='" + binding + '\'' +
                ", series='" + series + '\'' +
                ", average='" + average + '\'' +
                ", summary='" + summary + '\'' +
                ", authorIntro='" + authorIntro + '\'' +
                ", catalog='" + catalog + '\'' +
                ", tags='" + tags + '\'' +
                ", hasCrawled='" + hasCrawled + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originTitle='" + originTitle + '\'' +
                ", ebookPrice='" + ebookPrice + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", smallCover='" + smallCover + '\'' +
                ", mediumCover='" + mediumCover + '\'' +
                ", largeCover='" + largeCover + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", hasPublish=" + hasPublish +
                ", metaId='" + metaId + '\'' +
                '}';
    }
}
