package com.apabi.flow.nlcmarc.model;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/10/11 14:41
 **/
public class ApabiBookSeries {
    private String id;

    private String title;

    private String dataSource;

    private String collator;

    private String seriesType;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String publisher;

    private String summary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCollator() {
        return collator;
    }

    public void setCollator(String collator) {
        this.collator = collator;
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "ApabiBookSeries{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", collator='" + collator + '\'' +
                ", seriesType='" + seriesType + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", publisher='" + publisher + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
