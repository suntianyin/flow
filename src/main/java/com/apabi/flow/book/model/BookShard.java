package com.apabi.flow.book.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 14:06
 * @description
 */
@Table(name="APABI_BOOK_SHARD")
@Entity
public class BookShard {
    @Id
    @Column(name = "COMID")
    private String comId;

    @Column(name = "CHAPTERNUM")
    private Integer chapterNum;

    @Column(name = "INDEX1")
    private Integer index;

    @Column(name = "WORDSUM")
    private Integer wordSum;

    @Column(name = "BODYCLASS")
    private String bodyClass;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATETIME")
    private Date createTime;

    @Column(name = "UPDATETIME")
    private Date updateTime;

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
