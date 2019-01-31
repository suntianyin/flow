package com.apabi.flow.log.model;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.log.constant.DataType;
import com.apabi.flow.log.constant.OperateType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public class OperateLog {
    private String id;

    private String userId;

    private OperateType operateType;

    private DataType dataType;

    private String dataId;

    private String beforeOperate;

    private String afterOperate;

    private Date operateTime;

    public OperateLog(){
        this.setId(UUIDCreater.nextId());
        this.setOperateTime(new Date());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId == null ? null : dataId.trim();
    }

    public String getBeforeOperate() {
        return beforeOperate;
    }

    public void setBeforeOperate(String beforeOperate) {
        this.beforeOperate = beforeOperate;
    }

    public String getAfterOperate() {
        return afterOperate;
    }

    public void setAfterOperate(String afterOperate) {
        this.afterOperate = afterOperate;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}