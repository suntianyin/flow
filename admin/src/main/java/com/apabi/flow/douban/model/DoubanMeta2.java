package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author pipi
 * @date 2018/8/8 15:12
 * @description
 */
@Table(name = "DOUBAN_METADATA")
@Entity
public class DoubanMeta2 implements Serializable {
    @Id
    @Column(name = "DOUBANID")
    private String doubanId;

    @Column(name = "ISBN13")
    private String isbn13;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "ALT_TITLE")
    private String altTitle;

    @Column(name = "TRANSLATOR")
    private String translator;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "ISSUEDDATE")
    private String issueddate;

    @Column(name = "PAGES")
    private String pages;

    @Column(name = "PRICE")
    private String price;

    @Column(name = "BINDING")
    private String binding;

    @Column(name = "SERIES")
    private String series;

    @Column(name = "AVERAGE")
    private String average;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "AUTHOR_INTRO")
    private String authorIntro;

    @Column(name = "CATALOG")
    private String catalog;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "HASCRAWLED")
    private String hasCrawled;

    @Column(name = "ISBN10")
    private String ISBN10;

    @Column(name = "SUBTITLE")
    private String subtitle;

    @Column(name = "ORIGIN_TITLE")
    private String originTitle;

    @Column(name = "EBOOK_PRICE")
    private String ebookPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "CREATETIME")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "UPDATETIME")
    private Date updateTime;

    @Column(name = "SMALL_COVER")
    private String smallCover;

    @Column(name = "MEDIUM_COVER")
    private String mediumCover;

    @Column(name = "LARGE_COVER")
    private String largeCover;

    @Transient
    private String metaId;

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

    public String getISBN13() {
        return isbn13;
    }

    public void setISBN13(String isbn13) {
        this.isbn13 = isbn13;
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

    public String getISBN10() {
        return ISBN10;
    }

    public void setISBN10(String ISBN10) {
        this.ISBN10 = ISBN10;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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
                ", ISBN10='" + ISBN10 + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", originTitle='" + originTitle + '\'' +
                ", ebookPrice='" + ebookPrice + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", smallCover='" + smallCover + '\'' +
                ", mediumCover='" + mediumCover + '\'' +
                ", largeCover='" + largeCover + '\'' +
                ", metaId='" + metaId + '\'' +
                '}';
    }
}
