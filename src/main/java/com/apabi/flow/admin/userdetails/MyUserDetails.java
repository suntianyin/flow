package com.apabi.flow.admin.userdetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MyUserDetails extends User {
	
	private static final long serialVersionUID = -4024917057657058862L;
	
	private List<String> currentUserResCode = new ArrayList<>();

	public MyUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public List<String> getCurrentUserResCode() {
		return currentUserResCode;
	}

	public void setCurrentUserResCode(List<String> currentUserResCode) {
		this.currentUserResCode = currentUserResCode;
	}
	
}
