package com.apabi.flow.thematic.model;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/8/21 16:08
 **/
public class ThematicSeriesData {
    private String id;
    private String thematicId;
    private String metaId;
    private String title;
    private String author;
    private String isbn;
    private String isbn13;
    private String operator;
    private Date createTime;
    private Date updateTime;
    private String publisher;

    public ThematicSeriesData() {
    }

    public ThematicSeriesData(String id, String thematicId, String metaId, String title, String author, String publisher, String isbn, String isbn13, String operator, Date createTime, Date updateTime) {
        this.id = id;
        this.thematicId = thematicId;
        this.metaId = metaId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.operator = operator;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThematicId() {
        return thematicId;
    }

    public void setThematicId(String thematicId) {
        this.thematicId = thematicId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
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
        return "ThematicSeriesData{" +
                "id='" + id + '\'' +
                ", thematicId='" + thematicId + '\'' +
                ", metaId='" + metaId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
