package com.apabi.flow.nlcmarc.model;

import java.util.Date;

public class ApabiBookMetadataAuthor {
    private String id;

    private String metaId;

    private String authorType;

    private String name;

    private String type;

    private String pinyin;

    private String originalName;

    private String apabiAuthorId;

    private String nlcAuthorId;

    private String priority;

    private String nlcMarcIdentifier;

    private String addition;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String period;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getApabiAuthorId() {
        return apabiAuthorId;
    }

    public void setApabiAuthorId(String apabiAuthorId) {
        this.apabiAuthorId = apabiAuthorId;
    }

    public String getNlcAuthorId() {
        return nlcAuthorId;
    }

    public void setNlcAuthorId(String nlcAuthorId) {
        this.nlcAuthorId = nlcAuthorId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNlcMarcIdentifier() {
        return nlcMarcIdentifier;
    }

    public void setNlcMarcIdentifier(String nlcMarcIdentifier) {
        this.nlcMarcIdentifier = nlcMarcIdentifier;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
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
        return "ApabiBookMetadataAuthor{" +
                "id='" + id + '\'' +
                ", metaId='" + metaId + '\'' +
                ", authorType='" + authorType + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", originalName='" + originalName + '\'' +
                ", apabiAuthorId='" + apabiAuthorId + '\'' +
                ", nlcAuthorId='" + nlcAuthorId + '\'' +
                ", priority='" + priority + '\'' +
                ", nlcMarcIdentifier='" + nlcMarcIdentifier + '\'' +
                ", addition='" + addition + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", period='" + period + '\'' +
                '}';
    }
}