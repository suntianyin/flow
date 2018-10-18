package com.apabi.flow.douban.model;

/**
 * @Author pipi
 * @Date 2018/8/17 17:12
 **/
public class CompareEntity {
    private String tempValue;
    private String metaValue;
    private String fieldName;
    private String info;


    public CompareEntity() {
    }

    public CompareEntity(String tempValue, String metaValue, String fieldName, String info) {
        this.tempValue = tempValue;
        this.metaValue = metaValue;
        this.fieldName = fieldName;
        this.info = info;
    }

    public String getTempValue() {
        return tempValue;
    }

    public void setTempValue(String tempValue) {
        this.tempValue = tempValue;
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

    @Override
    public String toString() {
        return "CompareEntity{" +
                "tempValue='" + tempValue + '\'' +
                ", metaValue='" + metaValue + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
