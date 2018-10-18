package com.apabi.flow.admin.model;

public class AuthRoleGroup {

	private Integer id;
	private Integer groupId;
	private Integer roleId;
	private java.util.Date crtDate;
	private Integer crtUserId;
	
	private String roleCode;

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
	
	private AuthGroup authGroup;
	
	public void setAuthGroup(AuthGroup authGroup){
		this.authGroup = authGroup;
	}
	
	public AuthGroup getAuthGroup() {
		return authGroup;
	}
	
	private AuthRole authRole;
	
	public void setAuthRole(AuthRole authRole){
		this.authRole = authRole;
	}
	
	public AuthRole getAuthRole() {
		return authRole;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
}

