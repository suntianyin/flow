package com.apabi.flow.processing.model;

/**
 * @author supeng
 */
public class UserUnit {
    private String id;

    private Integer userId;

    private String unitId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId == null ? null : unitId.trim();
    }

    @Override
    public String toString() {
        return "UserUnit{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", unitId='" + unitId + '\'' +
                '}';
    }
}