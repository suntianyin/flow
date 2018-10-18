package com.apabi.flow.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author supeng
 * 图书分页内容 实体
 */
public class BookPage {
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    private String metaId;

    private Long pageId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    private Long wordSum;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId == null ? null : metaId.trim();
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getWordSum() {
        return wordSum;
    }

    public void setWordSum(Long wordSum) {
        this.wordSum = wordSum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        return "BookPage{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", metaId='" + metaId + '\'' +
                ", pageId=" + pageId +
                ", updateTime=" + updateTime +
                ", wordSum=" + wordSum +
                ", content='" + content + '\'' +
                '}';
    }
}