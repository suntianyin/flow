package com.apabi.flow.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 15:24
 * @description
 */
@Table(name = "APABI_BOOK_METADATA")
@Entity
public class EpubookMeta {
    @Id
    @Column(name = "METAID")
    private String metaid;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "CREATETIME")
    private Date createtime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "UPDATETIME")
    private Date updatetime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "ISSUEDDATE")
    private String issueddate;

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

    @Column(name = "CONTENTNUM")
    private Integer contentNum;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "ISOPTIMIZE")
    //private String isoptimize;
    private Integer isoptimize;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "STREAMCATALOG")
    private String streamCatalog;

    @Column(name = "COVERURL")
    private String coverUrl;

    @Column(name = "CHAPTERNUM")
    private Integer chapterNum;

    @Column(name = "STYLEURL")
    private String styleUrl;

    @Column(name = "THUMIMGURL")
    private String thumimgUrl;

    @Column(name = "STYLECLASS")
    private String styleClass;

    @Column(name = "HASFLOW")
    private Integer hasflow;

    private String flowSource;

    /*@Column(name = "FILENAME")
    private String fileName;*/

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

    public String getIssueddate() {
        return issueddate;
    }

    public void setIssueddate(String issueddate) {
        this.issueddate = issueddate;
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

    public Integer getContentNum() {
        return contentNum;
    }

    public void setContentNum(Integer contentNum) {
        this.contentNum = contentNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIsoptimize() {
        return isoptimize;
    }

    public void setIsoptimize(Integer isoptimize) {
        this.isoptimize = isoptimize;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStreamCatalog() {
        return streamCatalog;
    }

    public void setStreamCatalog(String streamCatalog) {
        this.streamCatalog = streamCatalog;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getThumimgUrl() {
        return thumimgUrl;
    }

    public void setThumimgUrl(String thumimgUrl) {
        this.thumimgUrl = thumimgUrl;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public Integer getHasflow() {
        return hasflow;
    }

    public void setHasflow(Integer hasflow) {
        this.hasflow = hasflow;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }
}
