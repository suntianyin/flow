package com.apabi.flow.common;

/**
 * @author guanpp
 * @date 2018/8/28 10:51
 * @description
 */
public class CommEntity {

    private String filedName;
    private Object filedValue;
    private String filedDesc;

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public Object getFiledValue() {
        return filedValue;
    }

    public void setFiledValue(Object filedValue) {
        this.filedValue = filedValue;
    }

    public String getFiledDesc() {
        return filedDesc;
    }

    public void setFiledDesc(String filedDesc) {
        this.filedDesc = filedDesc;
    }
}
