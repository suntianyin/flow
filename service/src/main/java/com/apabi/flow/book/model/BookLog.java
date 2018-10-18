package com.apabi.flow.book.model;

import java.util.Date;

public class BookLog {
    private String id;

    private Integer addedNum;

    private Date createTime;

    private String dataType;

    private Integer endIndex;

    private String metaId;

    private Integer startIndex;

    private Integer totals;

    private Date updateTime;

    public BookLog() {
    }

    public BookLog(String id,String metaId, String dataType, Integer addedNum,Integer totals, Integer startIndex, Integer endIndex, Date createTime, Date updateTime) {
        this.id = id;
        this.addedNum = addedNum;
        this.createTime = createTime;
        this.dataType = dataType;
        this.endIndex = endIndex;
        this.metaId = metaId;
        this.startIndex = startIndex;
        this.totals = totals;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getAddedNum() {
        return addedNum;
    }

    public void setAddedNum(Integer addedNum) {
        this.addedNum = addedNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId == null ? null : metaId.trim();
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getTotals() {
        return totals;
    }

    public void setTotals(Integer totals) {
        this.totals = totals;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BookLog{" +
                "id='" + id + '\'' +
                ", addedNum=" + addedNum +
                ", createTime=" + createTime +
                ", dataType='" + dataType + '\'' +
                ", endIndex=" + endIndex +
                ", metaId='" + metaId + '\'' +
                ", startIndex=" + startIndex +
                ", totals=" + totals +
                ", updateTime=" + updateTime +
                '}';
    }
}