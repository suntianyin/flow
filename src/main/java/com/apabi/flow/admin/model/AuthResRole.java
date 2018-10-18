package com.apabi.flow.admin.model;

public class AuthResRole {

	private Integer id;
	private Integer resId;
	private Integer roleId;
	private java.util.Date crtDate;
	private Integer crtUserId;

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	public void setResId(Integer value) {
		this.resId = value;
	}
	
	public Integer getResId() {
		return this.resId;
	}
	public void setRoleId(Integer value) {
		this.roleId = value;
	}
	
	public Integer getRoleId() {
		return this.roleId;
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
	
	private AuthRole authRole;

	public void setAuthRole(AuthRole authRole){
		this.authRole = authRole;
	}

	public AuthRole getAuthRole() {
		return authRole;
	}

	private AuthRes authRes;
	
	public void setAuthRes(AuthRes authRes){
		this.authRes = authRes;
	}
	
	public AuthRes getAuthRes() {
		return authRes;
	}
}

