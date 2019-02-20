package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author pipi
 */
public class AmazonMeta {
    private String amazonId;

    private String title;

    private String author;

    private String publisher;

    private String translator;

    private String isbn13;

    private String isbn10;

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

    private String postScript;

    private String readerObject;

    private Integer hasCrawled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    public String getAmazonId() {
        return amazonId;
    }

    public void setAmazonId(String amazonId) {
        this.amazonId = amazonId;
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

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice;
    }

    public String getKindlePrice() {
        return kindlePrice;
    }

    public void setKindlePrice(String kindlePrice) {
        this.kindlePrice = kindlePrice;
    }

    public String getEditionOrder() {
        return editionOrder;
    }

    public void setEditionOrder(String editionOrder) {
        this.editionOrder = editionOrder;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getOriginSeries() {
        return originSeries;
    }

    public void setOriginSeries(String originSeries) {
        this.originSeries = originSeries;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getCommodityWeight() {
        return commodityWeight;
    }

    public void setCommodityWeight(String commodityWeight) {
        this.commodityWeight = commodityWeight;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getEditRecommend() {
        return editRecommend;
    }

    public void setEditRecommend(String editRecommend) {
        this.editRecommend = editRecommend;
    }

    public String getCelebrityRecommend() {
        return celebrityRecommend;
    }

    public void setCelebrityRecommend(String celebrityRecommend) {
        this.celebrityRecommend = celebrityRecommend;
    }

    public String getMediaRecommendation() {
        return mediaRecommendation;
    }

    public void setMediaRecommendation(String mediaRecommendation) {
        this.mediaRecommendation = mediaRecommendation;
    }

    public String getAuthorIntroduction() {
        return authorIntroduction;
    }

    public void setAuthorIntroduction(String authorIntroduction) {
        this.authorIntroduction = authorIntroduction;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getAbstract_() {
        return abstract_;
    }

    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getReaderObject() {
        return readerObject;
    }

    public void setReaderObject(String readerObject) {
        this.readerObject = readerObject;
    }

    public Integer getHasCrawled() {
        return hasCrawled;
    }

    public void setHasCrawled(Integer hasCrawled) {
        this.hasCrawled = hasCrawled;
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

    public String getPostScript() {
        return postScript;
    }

    public void setPostScript(String postScript) {
        this.postScript = postScript;
    }
}