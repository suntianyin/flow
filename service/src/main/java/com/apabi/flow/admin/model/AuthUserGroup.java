package com.apabi.flow.admin.model;

public class AuthUserGroup {
	
	private Integer id;
	private Integer groupId;
	private Integer userId;
	private java.util.Date crtDate;
	private Integer crtUserId;

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	public void setGroupId(Integer value) {
		this.groupId = value;
	}
	
	public Integer getGroupId() {
		return this.groupId;
	}
	public void setUserId(Integer value) {
		this.userId = value;
	}
	
	public Integer getUserId() {
		return this.userId;
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
	
	private AuthGroup authGroup;
	
	public void setAuthGroup(AuthGroup authGroup){
		this.authGroup = authGroup;
	}
	
	public AuthGroup getAuthGroup() {
		return authGroup;
	}
	
	private AuthUser authUser;
	
	public void setAuthUser(AuthUser authUser){
		this.authUser = authUser;
	}
	
	public AuthUser getAuthUser() {
		return authUser;
	}
}

