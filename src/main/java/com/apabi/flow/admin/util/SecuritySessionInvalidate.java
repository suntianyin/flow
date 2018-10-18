package com.apabi.flow.admin.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.apabi.flow.admin.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

/**
 * 强制使session过期
 * @author houys
 *
 */
@Component
public class SecuritySessionInvalidate {

	@Autowired
	private SessionRegistry sessionRegistry;
	
	public void removeSession(List<AuthUser> loginNameLists) {
		List<Object> sr = sessionRegistry.getAllPrincipals();

		for(AuthUser loginUserTemp : loginNameLists){
			User loginUser = new User(loginUserTemp.getUserName(), "", false, false, false, false, new ArrayList<GrantedAuthority>(0));
		    Integer index = sr.indexOf(loginUser);
		    
		    if(index >= 0){
		    	User userDetail = (User)sr.get(index);
		    	List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(userDetail, false);
				for (SessionInformation sessionInformation : sessionInformations) {
					sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());    
					sessionInformation.expireNow();
				}
		    }
		}
	}
	
}
