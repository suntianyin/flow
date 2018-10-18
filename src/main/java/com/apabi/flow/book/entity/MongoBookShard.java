package com.apabi.flow.book.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author guanpp
 * @date 2018/9/5 17:20
 * @description
 */
@Document(collection = "book_shard")
public class MongoBookShard {
    @Field("id")
    private String id;
    @Field("chapterNum")
    private Integer chapterNum;
    @Field("index")
    private Integer index;
    @Field("wordSum")
    private Integer wordSum;
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

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getWordSum() {
        return wordSum;
    }

    public void setWordSum(Integer wordSum) {
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
