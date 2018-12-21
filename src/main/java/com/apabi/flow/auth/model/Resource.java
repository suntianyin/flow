package com.apabi.flow.auth.model;

import com.apabi.flow.auth.constant.ResourceStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Resource implements Serializable {
    private String resrId;

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

    private Integer assignPercent;

    private String copyrightOwner;

    private String isB2b;

    private String isCloud;

    private Integer authType;

    private ResourceStatusEnum status;

    private Date authStartDate;

    private Date authEndDate;

    private String freereadPercent;

    private Integer isCopyNum;

    private Integer isDatabaseSale;

    private Integer isChapterSale;

    private Integer isTimeSale;

    private Integer isFreeRead;

    private Integer isPod;

    private Integer isSerialise;

    private String other;

    private Integer hasOriginaledition;

    private Integer hasFlow;

    private Integer hasDatabase;

    private Integer hasMultimedia;

    private Integer hasPod;

    private String hasOther;

    private Date ncStartDate;

    private Date ncEndDate;

    private Integer isPublicedition;

    private Integer isFormatexpire;

    private String remark;

    private String batchNum;

    private String identifier;

    private String operator;

    private Date operateDate;


    private static final long serialVersionUID = 1L;

    public String getResrId() {
        return resrId;
    }

    public void setResrId(String resrId) {
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

    public Integer getAssignPercent() {
        return assignPercent;
    }

    public void setAssignPercent(Integer assignPercent) {
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

    public Date getAuthStartDate() {
        return authStartDate;
    }

    public void setAuthStartDate(Date authStartDate) {
        this.authStartDate = authStartDate;
    }

    public Date getAuthEndDate() {
        return authEndDate;
    }

    public void setAuthEndDate(Date authEndDate) {
        this.authEndDate = authEndDate;
    }

    public String getFreereadPercent() {
        return freereadPercent;
    }

    public void setFreereadPercent(String freereadPercent) {
        this.freereadPercent = freereadPercent;
    }

    public Integer getIsCopyNum() {
        return isCopyNum;
    }

    public void setIsCopyNum(Integer isCopyNum) {
        this.isCopyNum = isCopyNum;
    }

    public Integer getIsDatabaseSale() {
        return isDatabaseSale;
    }

    public void setIsDatabaseSale(Integer isDatabaseSale) {
        this.isDatabaseSale = isDatabaseSale;
    }

    public Integer getIsChapterSale() {
        return isChapterSale;
    }

    public void setIsChapterSale(Integer isChapterSale) {
        this.isChapterSale = isChapterSale;
    }

    public Integer getIsTimeSale() {
        return isTimeSale;
    }

    public void setIsTimeSale(Integer isTimeSale) {
        this.isTimeSale = isTimeSale;
    }

    public Integer getIsFreeRead() {
        return isFreeRead;
    }

    public void setIsFreeRead(Integer isFreeRead) {
        this.isFreeRead = isFreeRead;
    }

    public Integer getIsPod() {
        return isPod;
    }

    public void setIsPod(Integer isPod) {
        this.isPod = isPod;
    }

    public Integer getIsSerialise() {
        return isSerialise;
    }

    public void setIsSerialise(Integer isSerialise) {
        this.isSerialise = isSerialise;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Integer getHasOriginaledition() {
        return hasOriginaledition;
    }

    public void setHasOriginaledition(Integer hasOriginaledition) {
        this.hasOriginaledition = hasOriginaledition;
    }

    public Integer getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(Integer hasFlow) {
        this.hasFlow = hasFlow;
    }

    public Integer getHasDatabase() {
        return hasDatabase;
    }

    public void setHasDatabase(Integer hasDatabase) {
        this.hasDatabase = hasDatabase;
    }

    public Integer getHasMultimedia() {
        return hasMultimedia;
    }

    public void setHasMultimedia(Integer hasMultimedia) {
        this.hasMultimedia = hasMultimedia;
    }

    public Integer getHasPod() {
        return hasPod;
    }

    public void setHasPod(Integer hasPod) {
        this.hasPod = hasPod;
    }

    public String getHasOther() {
        return hasOther;
    }

    public void setHasOther(String hasOther) {
        this.hasOther = hasOther;
    }

    public Date getNcStartDate() {
        return ncStartDate;
    }

    public void setNcStartDate(Date ncStartDate) {
        this.ncStartDate = ncStartDate;
    }

    public Date getNcEndDate() {
        return ncEndDate;
    }

    public void setNcEndDate(Date ncEndDate) {
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
}