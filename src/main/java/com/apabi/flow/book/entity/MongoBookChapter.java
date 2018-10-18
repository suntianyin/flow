package com.apabi.flow.book.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/23 16:13
 * @description
 */
@Document(collection = "book_chapter")
public class MongoBookChapter {

    @Field("id")
    private String id;
    @Field("chapterNum")
    private int chapterNum;
    @Field("shardSum")
    private int shardSum;
    @Field("wordSum")
    private int wordSum;
    @Field("bodyClass")
    private String bodyClass;
    @Field("content")
    private String content;
    @Field("createdDate")
    private String createdDate;
    @Field("lastModifiedDate")
    private String lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public int getShardSum() {
        return shardSum;
    }

    public void setShardSum(int shardSum) {
        this.shardSum = shardSum;
    }

    public int getWordSum() {
        return wordSum;
    }

    public void setWordSum(int wordSum) {
        this.wordSum = wordSum;
    }

    public String getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
