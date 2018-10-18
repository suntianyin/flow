package com.apabi.flow.douban.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author pipi
 * @date 2018/8/8 17:23
 * @description
 */
@Table(name = "DOUBAN_APABI_FIELD_MAPPING")
@Entity
public class FieldMapping {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "APABI_META")
    private String apabiMeta;

    @Column(name = "DOUBAN_MEATA")
    private String doubanMeta;

    @Column(name = "META_DESC")
    private String metaDesc;

    @Column(name = "CREATETIME")
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApabiMeta() {
        return apabiMeta;
    }

    public void setApabiMeta(String apabiMeta) {
        this.apabiMeta = apabiMeta;
    }

    public String getDoubanMeta() {
        return doubanMeta;
    }

    public void setDoubanMeta(String doubanMeta) {
        this.doubanMeta = doubanMeta;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
