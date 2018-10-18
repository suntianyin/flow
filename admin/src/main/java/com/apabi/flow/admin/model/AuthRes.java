package com.apabi.flow.admin.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

public class AuthRes {
	

	public static final String TABLE_ALIAS = "AuthRes";
	public static final String ALIAS_ID = "id";
	public static final String ALIAS_MENU_ID = "所属菜单id";
	public static final String ALIAS_PARENT_ID = "父级资源id";
	public static final String ALIAS_CODE = "资源编码,系统中当做控制编码使用";
	public static final String ALIAS_NAME = "资源名称";
	public static final String ALIAS_URL = "资源链接";
	public static final String ALIAS_TYPE = "资源类型:0 接口资源，1 按钮资源，2 模块资源";
	public static final String ALIAS_VIEW_ORDER = "资源显示顺序";
	public static final String ALIAS_DESC = "按钮描述";
	public static final String ALIAS_CRT_DATE = "创建时间";
	public static final String ALIAS_CRT_USER_ID = "创建人id";
	public static final String ALIAS_MODIFY_DATE = "修改时间";
	public static final String ALIAS_MODIFY_USER_ID = "修改人id";
	public static final String ALIAS_ENABLED = "按钮状态";
	
	private Integer id;
	private Integer menuId;
	private Integer parentId;
	private String code;
	private String name;
	private String url;
	private String roleCode;
	private Integer type;
	private Integer viewOrder;
	private String description;
	@DateTimeFormat( pattern = "yyyy-MM-dd" )
	private java.util.Date crtDate;
	private Integer crtUserId;
	@DateTimeFormat( pattern = "yyyy-MM-dd" )
	private java.util.Date modifyDate;
	private Integer modifyUserId;
	private Integer enabled;

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	public void setMenuId(Integer value) {
		this.menuId = value;
	}
	
	public Integer getMenuId() {
		return this.menuId;
	}
	public void setParentId(Integer value) {
		this.parentId = value;
	}
	
	public Integer getParentId() {
		return this.parentId;
	}
	public void setCode(String value) {
		this.code = value;
	}
	
	public String getCode() {
		return this.code;
	}
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getUrl() {
		return this.url;
	}
	public void setType(Integer value) {
		this.type = value;
	}
	
	public Integer getType() {
		return this.type;
	}
	public void setViewOrder(Integer value) {
		this.viewOrder = value;
	}
	
	public Integer getViewOrder() {
		return this.viewOrder;
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
	
	private Set<AuthResRole> authResRoles = new HashSet<>(0);
	public void setAuthResRoles(Set<AuthResRole> authResRole) {
		this.authResRoles = authResRole;
	}
	
	public Set<AuthResRole> getAuthResRoles() {
		return authResRoles;
	}
	
	private Menu menu;
	
	public void setMenu(Menu menu){
		this.menu = menu;
	}
	
	public Menu getMenu() {
		return menu;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
}

