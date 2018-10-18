package com.apabi.flow.admin.model;

import java.util.HashSet;
import java.util.Set;

public class Menu {
	
	private Integer id;
	private Integer parentId;
	private String name;
	private String description;
	private java.util.Date crtDate;
	private Integer crtUserId;
	private java.util.Date modifyDate;
	private Integer modifyUserId;
	private Integer enabled;

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	public void setParentId(Integer value) {
		this.parentId = value;
	}
	
	public Integer getParentId() {
		return this.parentId;
	}
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setCrtDate(java.util.Date value) {
		this.crtDate = value;
	}
	
	public java.util.Date getCrtDate() {
		return this.crtDate;
	}
	public void setCrtUserId(Integer value) {
		this.crtUserId = value;
	}
	
	public Integer getCrtUserId() {
		return this.crtUserId;
	}
	
	public void setModifyDate(java.util.Date value) {
		this.modifyDate = value;
	}
	
	public java.util.Date getModifyDate() {
		return this.modifyDate;
	}
	public void setModifyUserId(Integer value) {
		this.modifyUserId = value;
	}
	
	public Integer getModifyUserId() {
		return this.modifyUserId;
	}
	public void setEnabled(Integer value) {
		this.enabled = value;
	}
	
	public Integer getEnabled() {
		return this.enabled;
	}
	
	private Set<AuthRes> authRess = new HashSet<>(0);
	public void setAuthRess(Set<AuthRes> authRes) {
		this.authRess = authRes;
	}
	
	public Set<AuthRes> getAuthRess() {
		return authRess;
	}
}

