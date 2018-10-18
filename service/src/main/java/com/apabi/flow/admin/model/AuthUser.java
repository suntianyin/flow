package com.apabi.flow.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AuthUser {

    //alias
    public static final String TABLE_ALIAS = "AuthUser";
    public static final String ALIAS_ID = "id";
    public static final String ALIAS_USER_NAME = "用户登录名";
    public static final String ALIAS_PASSWORD = "用户密码";
    public static final String ALIAS_ORG_ID = "机构id";
    public static final String ALIAS_REAL_NAME = "用户真实姓名";
    public static final String ALIAS_GENDER = "性别";
    public static final String ALIAS_BIRTHDATE = "出生日期";
    public static final String ALIAS_TELEPHONE = "联系电话";
    public static final String ALIAS_EMAIL = "邮箱";
    public static final String ALIAS_MODIFY_PASSWORD_DATE = "密码更改时间";
    public static final String ALIAS_CRT_DATE = "用户创建时间";
    public static final String ALIAS_CRT_USER_ID = "用户创建人id";
    public static final String ALIAS_MODIFY_DATE = "修改日期";
    public static final String ALIAS_MODIFY_USER_ID = "修改人id";
    public static final String ALIAS_ENABLED = "用户状态";

    public static final Integer ENABLED = 1;
    public static final Integer DISABLED = 0;

    private java.lang.Integer id;
    private java.lang.String userName;
    private java.lang.String password;
    private java.lang.Integer orgId;
    private java.lang.String realName;
    private Integer gender;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date birthdate;
    private java.lang.String telephone;
    private java.lang.String email;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date modifyPasswordDate;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date crtDate;
    private java.lang.Integer crtUserId;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date modifyDate;
    private java.lang.Integer modifyUserId;
    private Integer enabled;

    private List<Integer> groupIds;

    public void setId(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return this.id;
    }
    public void setUserName(java.lang.String value) {
        this.userName = value;
    }

    public java.lang.String getUserName() {
        return this.userName;
    }
    public void setPassword(java.lang.String value) {
        this.password = value;
    }

    public java.lang.String getPassword() {
        return this.password;
    }
    public void setOrgId(java.lang.Integer value) {
        this.orgId = value;
    }

    public java.lang.Integer getOrgId() {
        return this.orgId;
    }
    public void setRealName(java.lang.String value) {
        this.realName = value;
    }

    public java.lang.String getRealName() {
        return this.realName;
    }
    public void setGender(Integer value) {
        this.gender = value;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setBirthdate(java.util.Date value) {
        this.birthdate = value;
    }

    public java.util.Date getBirthdate() {
        return this.birthdate;
    }
    public void setTelephone(java.lang.String value) {
        this.telephone = value;
    }

    public java.lang.String getTelephone() {
        return this.telephone;
    }
    public void setEmail(java.lang.String value) {
        this.email = value;
    }

    public java.lang.String getEmail() {
        return this.email;
    }

    public void setModifyPasswordDate(java.util.Date value) {
        this.modifyPasswordDate = value;
    }

    public java.util.Date getModifyPasswordDate() {
        return this.modifyPasswordDate;
    }

    public void setCrtDate(java.util.Date value) {
        this.crtDate = value;
    }

    public java.util.Date getCrtDate() {
        return this.crtDate;
    }
    public void setCrtUserId(java.lang.Integer value) {
        this.crtUserId = value;
    }

    public java.lang.Integer getCrtUserId() {
        return this.crtUserId;
    }

    public void setModifyDate(java.util.Date value) {
        this.modifyDate = value;
    }

    public java.util.Date getModifyDate() {
        return this.modifyDate;
    }
    public void setModifyUserId(java.lang.Integer value) {
        this.modifyUserId = value;
    }

    public java.lang.Integer getModifyUserId() {
        return this.modifyUserId;
    }
    public void setEnabled(Integer value) {
        this.enabled = value;
    }

    public Integer getEnabled() {
        return this.enabled;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

}
