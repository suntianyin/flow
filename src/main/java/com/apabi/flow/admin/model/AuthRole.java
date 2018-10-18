package com.apabi.flow.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AuthRole {

    private java.lang.Integer id;
    private java.lang.String roleCode;
    private java.lang.Integer orgId;
    private java.lang.String name;
    private java.lang.String description;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date crtDate;
    private java.lang.Integer crtUserId;
    @DateTimeFormat( pattern = "yyyy-MM-dd" )
    private java.util.Date modifyDate;
    private java.lang.Integer modifyUserId;
    private Integer enabled;

    public java.lang.String getDescription() {
        return description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public void setId(java.lang.Integer value) {
        this.id = value;
    }

    public java.lang.Integer getId() {
        return this.id;
    }
    public void setRoleCode(java.lang.String value) {
        this.roleCode = value;
    }

    public java.lang.String getRoleCode() {
        return this.roleCode;
    }
    public void setOrgId(java.lang.Integer value) {
        this.orgId = value;
    }

    public java.lang.Integer getOrgId() {
        return this.orgId;
    }
    public void setName(java.lang.String value) {
        this.name = value;
    }

    public java.lang.String getName() {
        return this.name;
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

    private Set<AuthRoleGroup> authRoleGroups = new HashSet<>(0);
    public void setAuthRoleGroups(Set<AuthRoleGroup> authRoleGroup) {
        this.authRoleGroups = authRoleGroup;
    }

    public Set<AuthRoleGroup> getAuthRoleGroups() {
        return authRoleGroups;
    }

    private Set<AuthResRole> authResRoles = new HashSet<>(0);
    public void setAuthResRoles(Set<AuthResRole> authResRole) {
        this.authResRoles = authResRole;
    }

    public Set<AuthResRole> getAuthResRoles() {
        return authResRoles;
    }

    private Org org;

    public void setOrg(Org org){
        this.org = org;
    }

    public Org getOrg() {
        return org;
    }
}
