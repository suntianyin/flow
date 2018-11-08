package com.apabi.flow.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author pipi
 * @date 2018/8/9 10:12
 */
@Entity
@Table(name = "APABI_BOOK_METADATA_TEMP")
public class ApabiBookMetaTemp2 implements Serializable {
    @Id
    @Column(name = "METAID")
    private String metaId;

    @Column(name = "DOUBANID")
    private String doubanId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "ISBN13")
    private String isbn13;

    @Column(name = "ISBN10")
    private String isbn10;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "ALTERNATIVETITLE")
    private String alternativeTitle;

    @Column(name = "SUBTITLE")
    private String subTitle;

    @Column(name = "ORIGINTITLE")
    private String originTitle;

    @Column(name = "TRANSLATOR")
    private String translator;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "ISSUEDDATE")
    private String issueddate;

    @Column(name = "BOOKPAGES")
    private String bookPages;

    @Column(name = "PAPERPRICE")
    private String paperPrice;

    @Column(name = "EBOOKPRICE")
    private String ebookPrice;

    @Column(name = "BINDING")
    private String binding;

    @Column(name = "RELATION")
    private String relation;

    @Column(name = "ABSTRACT")
    private String abstract_;

    @Column(name = "AUTHORINTRO")
    private String authorIntro;

    @Column(name = "FOAMATCATALOG")
    private String foamatCatalog;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "THUMIMGURL")
    private String thumImgUrl;

    @Column(name = "MEDIUMCOVER")
    private String mediumCover;

    @Column(name = "COVERURL")
    private String coverUrl;

    @Column(name = "DATASOURCE")
    private String dataSource="douban";

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "CREATETIME")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "UPDATETIME")
    private Date updateTime;

    @Column(name = "ISREADEPUB")
    private Integer isReadEpub;

    @Column(name = "FLOWSOURCE")
    private String flowSource;

    @Column(name = "ISREADCEBXFLOW")
    private Integer isReadCebxFlow;

    public Integer getIsReadEpub() {
        return isReadEpub;
    }

    public void setIsReadEpub(Integer isReadEpub) {
        this.isReadEpub = isReadEpub;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }

    public Integer getIsReadCebxFlow() {
        return isReadCebxFlow;
    }

    public void setIsReadCebxFlow(Integer isReadCebxFlow) {
        this.isReadCebxFlow = isReadCebxFlow;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
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

    public String getBookPages() {
        return bookPages;
    }

    public void setBookPages(String bookPages) {
        this.bookPages = bookPages;
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice;
    }

    public String getEbookPrice() {
        return ebookPrice;
    }

    public void setEbookPrice(String ebookPrice) {
        this.ebookPrice = ebookPrice;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAbstract_() {
        return abstract_;
    }

    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getFoamatCatalog() {
        return foamatCatalog;
    }

    public void setFoamatCatalog(String foamatCatalog) {
        this.foamatCatalog = foamatCatalog;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getThumImgUrl() {
        return thumImgUrl;
    }

    public void setThumImgUrl(String thumImgUrl) {
        this.thumImgUrl = thumImgUrl;
    }

    public String getMediumCover() {
        return mediumCover;
    }

    public void setMediumCover(String mediumCover) {
        this.mediumCover = mediumCover;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ApabiBookMetaTemp2() {
    }

    @Override
    public String toString() {
        return "ApabiBookMetaTemp2{" +
                "metaId='" + metaId + '\'' +
                ", doubanId='" + doubanId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", publisher='" + publisher + '\'' +
                ", alternativeTitle='" + alternativeTitle + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originTitle='" + originTitle + '\'' +
                ", translator='" + translator + '\'' +
                ", issueddate='" + issueddate + '\'' +
                ", bookPages='" + bookPages + '\'' +
                ", paperPrice='" + paperPrice + '\'' +
                ", ebookPrice='" + ebookPrice + '\'' +
                ", binding='" + binding + '\'' +
                ", relation='" + relation + '\'' +
                ", abstract_='" + abstract_ + '\'' +
                ", authorIntro='" + authorIntro + '\'' +
                ", foamatCatalog='" + foamatCatalog + '\'' +
                ", tags='" + tags + '\'' +
                ", thumImgUrl='" + thumImgUrl + '\'' +
                ", mediumCover='" + mediumCover + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isReadEpub=" + isReadEpub +
                ", flowSource='" + flowSource + '\'' +
                ", isReadCebxFlow=" + isReadCebxFlow +
                '}';
    }
}
