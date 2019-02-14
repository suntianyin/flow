package com.apabi.flow.book.model;

import java.util.Date;

/**
 * @author guanpp
 * @date 2018/7/31 13:28
 * @description
 */
public class BookChapterVo {
    private String id;

    private String comId;

    private Integer chapterNum;

    private Integer shardSum;

    private Integer wordSum;

    private String bodyClass;

    private String content;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public Integer getShardSum() {
        return shardSum;
    }

    public void setShardSum(Integer shardSum) {
        this.shardSum = shardSum;
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
}
