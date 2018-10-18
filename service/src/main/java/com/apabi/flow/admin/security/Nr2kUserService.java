package com.apabi.flow.admin.security;

import com.apabi.flow.admin.dao.AuthResDao;
import com.apabi.flow.admin.dao.AuthUserDao;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Nr2kUserService implements UserDetailsService { //自定义UserDetailsService 接口

    @Autowired
    AuthUserDao authUserDao;
    @Autowired
    AuthResDao authResDao;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AuthUser user = authUserDao.findByUserName(username);
        if (user != null) {
            List<AuthRes> authRess = authResDao.findByUserId(user.getId());
            List<GrantedAuthority> grantedAuthorities = new ArrayList <>();
            for (AuthRes authRes : authRess) {
                if (authRes != null && authRes.getName()!=null) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authRes.getName());
                    grantedAuthorities.add(grantedAuthority);
                }
            }
            return new User(user.getUserName(), user.getPassword(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
        }
    }

}