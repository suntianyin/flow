package com.apabi.flow.processing.model;

import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.constant.CompletedFlagEnum;
import com.apabi.flow.processing.constant.DeleteFlagEnum;
import com.apabi.flow.processing.constant.DuplicateFlagEnum;

import java.util.Date;

/**
 * @author supeng
 */
public class Bibliotheca {
    private String id;

    private String identifier;

    private String metaId;

    private String batchId;

    private String originalFilename;

    private String title;

    private String author;

    private String publisher;

    private String publisherName;

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

    private Date createTime;

    private Date updateTime;

    private DeleteFlagEnum deleteFlag;

    private Integer convertStatus = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier == null ? null : identifier.trim();
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId == null ? null : metaId.trim();
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename == null ? null : originalFilename.trim();
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId == null ? null : batchId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher == null ? null : publisher.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime == null ? null : publishTime.trim();
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition == null ? null : edition.trim();
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice == null ? null : paperPrice.trim();
    }

    public String geteBookPrice() {
        return eBookPrice;
    }

    public void seteBookPrice(String eBookPrice) {
        this.eBookPrice = eBookPrice == null ? null : eBookPrice.trim();
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat == null ? null : documentFormat.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
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
        this.creator = creator == null ? null : creator.trim();
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

    public DeleteFlagEnum getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(DeleteFlagEnum deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public Integer getConvertStatus() {
        return convertStatus;
    }

    public void setConvertStatus(Integer convertStatus) {
        this.convertStatus = convertStatus;
    }

    @Override
    public String toString() {
        return "Bibliotheca{" +
                "id='" + id + '\'' +
                ", identifier='" + identifier + '\'' +
                ", metaId='" + metaId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publisherName='" + publisherName + '\'' +
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
                ", updateTime=" + updateTime +
                ", deleteFlag=" + deleteFlag +
                ", convertStatus=" + convertStatus +
                '}';
    }

    public static void main(String[] args) {
        Bibliotheca bibliotheca = new Bibliotheca();
        bibliotheca.setConvertStatus(2);
        System.out.println(bibliotheca);
    }
}