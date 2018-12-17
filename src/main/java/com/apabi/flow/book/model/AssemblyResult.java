package com.apabi.flow.book.model;

import java.io.Serializable;
import java.util.Date;

public class AssemblyResult implements Serializable {
    private String id;

    private String metaid;
    //0失败1成功
    private Integer result;

    private String message;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMetaid() {
        return metaid;
    }

    public void setMetaid(String metaid) {
        this.metaid = metaid == null ? null : metaid.trim();
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}