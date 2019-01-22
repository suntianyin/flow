package com.apabi.flow.auth.model;

import com.apabi.flow.auth.constant.ResourceStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Resource implements Serializable {
    private Integer resrId;

    private String booklistNum;

    private Date insertDate;

    private String metaId;

    private String title;

    private String creator;

    private String publisher;

    private Date issuedDate;

    private String isbn;

    private String editionOrder;//

    private Double paperPrice;

    private Double ePrice;

    private String isonlyOwner;

    private Integer assignRule;

    private Double assignPercent;

    private String copyrightOwner;

    private String isB2b;

    private String isCloud;

    private Integer authType;

    private ResourceStatusEnum status;

    private String authStartDate;

    private String authEndDate;

    private String freereadPercent;

    private String isCopyNum;

    private String isDatabaseSale;

    private String isChapterSale;

    private String isTimeSale;

    private String isFreeRead;

    private String isPod;

    private String isSerialise;

    private String other;

    private String hasOriginaledition;

    private String hasFlow;

    private String hasDatabase;

    private String hasMultimedia;

    private String hasPod;

    private String hasOther;

    private String ncStartDate;

    private String ncEndDate;

    private Integer isPublicedition;

    private Integer isFormatexpire;

    private String remark;

    private String batchNum;

    private String identifier;

    private String operator;

    private Date operateDate;

    private String publisherId;

    private String copyrightOwnerId;


    private static final long serialVersionUID = 1L;

    public Integer getResrId() {
        return resrId;
    }

    public void setResrId(Integer resrId) {
        this.resrId = resrId;
    }

    public String getBooklistNum() {
        return booklistNum;
    }

    public void setBooklistNum(String booklistNum) {
        this.booklistNum = booklistNum;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
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

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditionOrder() {
        return editionOrder;
    }

    public void setEditionOrder(String editionOrder) {
        this.editionOrder = editionOrder;
    }

    public Double getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(Double paperPrice) {
        this.paperPrice = paperPrice;
    }

    public Double getePrice() {
        return ePrice;
    }

    public void setePrice(Double ePrice) {
        this.ePrice = ePrice;
    }

    public String getIsonlyOwner() {
        return isonlyOwner;
    }

    public void setIsonlyOwner(String isonlyOwner) {
        this.isonlyOwner = isonlyOwner;
    }

    public Integer getAssignRule() {
        return assignRule;
    }

    public void setAssignRule(Integer assignRule) {
        this.assignRule = assignRule;
    }

    public Double getAssignPercent() {
        return assignPercent;
    }

    public void setAssignPercent(Double assignPercent) {
        this.assignPercent = assignPercent;
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner;
    }

    public String getIsB2b() {
        return isB2b;
    }

    public void setIsB2b(String isB2b) {
        this.isB2b = isB2b;
    }

    public String getIsCloud() {
        return isCloud;
    }

    public void setIsCloud(String isCloud) {
        this.isCloud = isCloud;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public ResourceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResourceStatusEnum status) {
        this.status = status;
    }

    public String getAuthStartDate() {
        return authStartDate;
    }

    public void setAuthStartDate(String authStartDate) {
        this.authStartDate = authStartDate;
    }

    public String getAuthEndDate() {
        return authEndDate;
    }

    public void setAuthEndDate(String authEndDate) {
        this.authEndDate = authEndDate;
    }

    public String getFreereadPercent() {
        return freereadPercent;
    }

    public void setFreereadPercent(String freereadPercent) {
        this.freereadPercent = freereadPercent;
    }

    public String getIsCopyNum() {
        return isCopyNum;
    }

    public void setIsCopyNum(String isCopyNum) {
        this.isCopyNum = isCopyNum;
    }

    public String getIsDatabaseSale() {
        return isDatabaseSale;
    }

    public void setIsDatabaseSale(String isDatabaseSale) {
        this.isDatabaseSale = isDatabaseSale;
    }

    public String getIsChapterSale() {
        return isChapterSale;
    }

    public void setIsChapterSale(String isChapterSale) {
        this.isChapterSale = isChapterSale;
    }

    public String getIsTimeSale() {
        return isTimeSale;
    }

    public void setIsTimeSale(String isTimeSale) {
        this.isTimeSale = isTimeSale;
    }

    public String getIsFreeRead() {
        return isFreeRead;
    }

    public void setIsFreeRead(String isFreeRead) {
        this.isFreeRead = isFreeRead;
    }

    public String getIsPod() {
        return isPod;
    }

    public void setIsPod(String isPod) {
        this.isPod = isPod;
    }

    public String getIsSerialise() {
        return isSerialise;
    }

    public void setIsSerialise(String isSerialise) {
        this.isSerialise = isSerialise;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getHasOriginaledition() {
        return hasOriginaledition;
    }

    public void setHasOriginaledition(String hasOriginaledition) {
        this.hasOriginaledition = hasOriginaledition;
    }

    public String getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(String hasFlow) {
        this.hasFlow = hasFlow;
    }

    public String getHasDatabase() {
        return hasDatabase;
    }

    public void setHasDatabase(String hasDatabase) {
        this.hasDatabase = hasDatabase;
    }

    public String getHasMultimedia() {
        return hasMultimedia;
    }

    public void setHasMultimedia(String hasMultimedia) {
        this.hasMultimedia = hasMultimedia;
    }

    public String getHasPod() {
        return hasPod;
    }

    public void setHasPod(String hasPod) {
        this.hasPod = hasPod;
    }

    public String getHasOther() {
        return hasOther;
    }

    public void setHasOther(String hasOther) {
        this.hasOther = hasOther;
    }

    public String getNcStartDate() {
        return ncStartDate;
    }

    public void setNcStartDate(String ncStartDate) {
        this.ncStartDate = ncStartDate;
    }

    public String getNcEndDate() {
        return ncEndDate;
    }

    public void setNcEndDate(String ncEndDate) {
        this.ncEndDate = ncEndDate;
    }

    public Integer getIsPublicedition() {
        return isPublicedition;
    }

    public void setIsPublicedition(Integer isPublicedition) {
        this.isPublicedition = isPublicedition;
    }

    public Integer getIsFormatexpire() {
        return isFormatexpire;
    }

    public void setIsFormatexpire(Integer isFormatexpire) {
        this.isFormatexpire = isFormatexpire;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getCopyrightOwnerId() {
        return copyrightOwnerId;
    }

    public void setCopyrightOwnerId(String copyrightOwnerId) {
        this.copyrightOwnerId = copyrightOwnerId;
    }
}