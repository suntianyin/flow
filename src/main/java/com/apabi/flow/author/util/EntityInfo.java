package com.apabi.flow.author.util;

/**
 * @author: sunty
 * @date: 2018/10/31 17:10
 * @description:
 */

public class EntityInfo {
    private String metaValue;
    private String fieldName;
    private String info;

    public EntityInfo() {
    }

    public EntityInfo(String metaValue, String fieldName, String info) {
        this.metaValue = metaValue;
        this.fieldName = fieldName;
        this.info = info;
    }

    public String getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(String metaValue) {
        this.metaValue = metaValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
