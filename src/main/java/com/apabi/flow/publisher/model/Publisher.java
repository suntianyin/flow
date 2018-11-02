package com.apabi.flow.publisher.model;

import com.apabi.flow.publisher.constant.ClassCodeEnum;
import com.apabi.flow.publisher.constant.ResourceTypeEnum;
import com.apabi.flow.publisher.constant.TitleTypeEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuji
 * @date 2018/8/10 14:52
 * @description
 */
public class Publisher implements Serializable {

    private String id;
    private String relatePublisherID;
    private String isbn;
    private String title;
    private TitleTypeEnum titleType;
    private String startDate;
    private String endDate;
    private String nationalityCode;
    private String founderDate;
    private ClassCodeEnum classCode;
    private ResourceTypeEnum resourceType;
    private String publishingGroup;
    private String publishingGroupID;
    private String president;
    private String vicePresident;
    private String qualityLevel;
    private String summary;
    private String place;
    private String operator;
    private Date createTime;
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelatePublisherID() {
        return relatePublisherID;
    }

    public void setRelatePublisherID(String relatePublisherID) {
        this.relatePublisherID = relatePublisherID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public String getFounderDate() {
        return founderDate;
    }

    public void setFounderDate(String founderDate) {
        this.founderDate = founderDate;
    }

    public TitleTypeEnum getTitleType() {
        return titleType;
    }

    public void setTitleType(TitleTypeEnum titleType) {
        this.titleType = titleType;
    }

    public ClassCodeEnum getClassCode() {
        return classCode;
    }

    public void setClassCode(ClassCodeEnum classCode) {
        this.classCode = classCode;
    }

    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public String getPublishingGroup() {
        return publishingGroup;
    }

    public void setPublishingGroup(String publishingGroup) {
        this.publishingGroup = publishingGroup;
    }

    public String getPublishingGroupID() {
        return publishingGroupID;
    }

    public void setPublishingGroupID(String publishingGroupID) {
        this.publishingGroupID = publishingGroupID;
    }

    public String getPresident() {
        return president;
    }

    public void setPresident(String president) {
        this.president = president;
    }

    public String getVicePresident() {
        return vicePresident;
    }

    public void setVicePresident(String vicePresident) {
        this.vicePresident = vicePresident;
    }

    public String getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    @Override
    public String toString() {
        return "Publisher{" +
                "id='" + id + '\'' +
                ", relatePublisherID='" + relatePublisherID + '\'' +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", titleType='" + titleType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", nationalityCode='" + nationalityCode + '\'' +
                ", founderDate='" + founderDate + '\'' +
                ", classCode='" + classCode + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", publishingGroup='" + publishingGroup + '\'' +
                ", publishingGroupID='" + publishingGroupID + '\'' +
                ", president='" + president + '\'' +
                ", vicePresident='" + vicePresident + '\'' +
                ", qualityLevel='" + qualityLevel + '\'' +
                ", summary='" + summary + '\'' +
                ", place='" + place + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
