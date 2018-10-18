package com.apabi.flow.thematic.model;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/8/21 16:08
 **/
public class ThematicSeries {
    private String id;
    private String title;
    private String dataSource;
    private String collator;
    private String operator;
    private Date createTime;
    private Date updateTime;

    public ThematicSeries() {
    }

    public ThematicSeries(String id, String title, String dataSource, String collator, String operator, Date createTime, Date updateTime) {
        this.id = id;
        this.title = title;
        this.dataSource = dataSource;
        this.collator = collator;
        this.operator = operator;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

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
        return "ThematicSeries{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", collator='" + collator + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
