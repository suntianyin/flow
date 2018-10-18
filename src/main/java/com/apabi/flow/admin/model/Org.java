package com.apabi.flow.admin.model;

import java.io.Serializable;
import java.util.Date;

public class Org implements Serializable {
    private Integer id;

    private String orgCode;

    private String orgName;

    private String description;

    private Date crtDate;

    private Byte enabled;

    private Date updateDate;

    private Date endDate;

    private Integer deviceNum;

    private String areaCode;

    private String areaName;

    private String type;

    private String syId;

    private String rightKey;

//    private List<OrderForm> orderForms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCrtDate() {
        return crtDate;
    }

    public void setCrtDate(Date crtDate) {
        this.crtDate = crtDate;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSyId() {
        return syId;
    }

    public void setSyId(String syId) {
        this.syId = syId == null ? null : syId.trim();
    }

    public String getRightKey() {
        return rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey == null ? null : rightKey.trim();
    }

//    public List<OrderForm> getOrderForms() {
//        return orderForms;
//    }
//
//    public void setOrderForms(List<OrderForm> orderForms) {
//        this.orderForms = orderForms;
//    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}