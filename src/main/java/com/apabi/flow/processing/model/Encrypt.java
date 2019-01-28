package com.apabi.flow.processing.model;

import com.apabi.flow.processing.constant.EncryptStateEnum;

import java.io.Serializable;
import java.util.Date;

public class Encrypt implements Serializable {
    private String id;

    private String taskName;

    private String batch;

    private Integer encryptNum;

    private Integer finishNum;

    private EncryptStateEnum taskState;

    private Date createTime;

    private Date finishTime;

    private String operator;

    private Double pre;

    private static final long serialVersionUID = 1L;

    public Double getPre() {
        return pre;
    }

    public void setPre(Double pre) {
        this.pre = pre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch == null ? null : batch.trim();
    }

    public Integer getEncryptNum() {
        return encryptNum;
    }

    public void setEncryptNum(Integer encryptNum) {
        this.encryptNum = encryptNum;
    }

    public Integer getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(Integer finishNum) {
        this.finishNum = finishNum;
    }

    public EncryptStateEnum getTaskState() {
        return taskState;
    }

    public void setTaskState(EncryptStateEnum taskState) {
        this.taskState = taskState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }
}