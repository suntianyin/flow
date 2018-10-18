package com.apabi.flow.processing.model;

import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.constant.CompletedFlagEnum;
import com.apabi.flow.processing.constant.DuplicateFlagEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 功能描述： <br>
 * <书目查询结果 excel 导出实体>
 *
 * @author supeng
 * @date 2018/9/26 17:55
 * @since 1.0.0
 */
public class BibliothecaExcelModel {

    private String identifier;

    private String metaId;

    private String batchId;

    private String originalFilename;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private String publishTime;

    private String edition;

    private String paperPrice;

    private String eBookPrice;

    private String documentFormat;

    private String memo;

    private DuplicateFlagEnum duplicateFlag;

    private BibliothecaStateEnum bibliothecaState;

    private CompletedFlagEnum completedFlag;

    private String creator;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    public BibliothecaExcelModel() {
    }

    public BibliothecaExcelModel(String identifier, String metaId, String batchId, String originalFilename,
                                 String title, String author, String publisher, String isbn, String publishTime,
                                 String edition, String paperPrice, String eBookPrice, String documentFormat,
                                 String memo, DuplicateFlagEnum duplicateFlag, BibliothecaStateEnum bibliothecaState,
                                 CompletedFlagEnum completedFlag, String creator, Date createTime) {
        this.identifier = identifier;
        this.metaId = metaId;
        this.batchId = batchId;
        this.originalFilename = originalFilename;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.publishTime = publishTime;
        this.edition = edition;
        this.paperPrice = paperPrice;
        this.eBookPrice = eBookPrice;
        this.documentFormat = documentFormat;
        this.memo = memo;
        this.duplicateFlag = duplicateFlag;
        this.bibliothecaState = bibliothecaState;
        this.completedFlag = completedFlag;
        this.creator = creator;
        this.createTime = createTime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
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

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice;
    }

    public String geteBookPrice() {
        return eBookPrice;
    }

    public void seteBookPrice(String eBookPrice) {
        this.eBookPrice = eBookPrice;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public DuplicateFlagEnum getDuplicateFlag() {
        return duplicateFlag;
    }

    public void setDuplicateFlag(DuplicateFlagEnum duplicateFlag) {
        this.duplicateFlag = duplicateFlag;
    }

    public BibliothecaStateEnum getBibliothecaState() {
        return bibliothecaState;
    }

    public void setBibliothecaState(BibliothecaStateEnum bibliothecaState) {
        this.bibliothecaState = bibliothecaState;
    }

    public CompletedFlagEnum getCompletedFlag() {
        return completedFlag;
    }

    public void setCompletedFlag(CompletedFlagEnum completedFlag) {
        this.completedFlag = completedFlag;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BibliothecaExcelModel{" +
                "identifier='" + identifier + '\'' +
                ", metaId='" + metaId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", edition='" + edition + '\'' +
                ", paperPrice='" + paperPrice + '\'' +
                ", eBookPrice='" + eBookPrice + '\'' +
                ", documentFormat='" + documentFormat + '\'' +
                ", memo='" + memo + '\'' +
                ", duplicateFlag=" + duplicateFlag +
                ", bibliothecaState=" + bibliothecaState +
                ", completedFlag=" + completedFlag +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
