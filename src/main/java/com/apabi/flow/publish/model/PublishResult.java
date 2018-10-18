package com.apabi.flow.publish.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PublishResult {
    private String id;

    private String operateDataType;

    private String metaId;

    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    private String operateResult;

    private String dataSource;

    private String preContent;

    private String postContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer hasSync;


    public PublishResult() {
    }

    public PublishResult(String id, String operateDataType, String metaId, String operator, Date operateTime, String operateResult, String dataSource, String preContent, String postContent, Date updateTime, Date createTime, Integer hasSync) {
        this.id = id;
        this.operateDataType = operateDataType;
        this.metaId = metaId;
        this.operator = operator;
        this.operateTime = operateTime;
        this.operateResult = operateResult;
        this.dataSource = dataSource;
        this.preContent = preContent;
        this.postContent = postContent;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.hasSync = hasSync;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperateDataType() {
        return operateDataType;
    }

    public void setOperateDataType(String operateDataType) {
        this.operateDataType = operateDataType;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getPreContent() {
        return preContent;
    }

    public void setPreContent(String preContent) {
        this.preContent = preContent;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
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

    public Integer getHasSync() {
        return hasSync;
    }

    public void setHasSync(Integer hasSync) {
        this.hasSync = hasSync;
    }

    @Override
    public String toString() {
        return "PublishResult{" +
                "id='" + id + '\'' +
                ", operateDataType='" + operateDataType + '\'' +
                ", metaId='" + metaId + '\'' +
                ", operator='" + operator + '\'' +
                ", operateTime=" + operateTime +
                ", operateResult='" + operateResult + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", preContent='" + preContent + '\'' +
                ", postContent='" + postContent + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", hasSync=" + hasSync +
                '}';
    }
}