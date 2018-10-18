package com.apabi.flow.config;

import com.apabi.flow.admin.security.Nr2kFilterSecurityInterceptor;
import com.apabi.flow.book.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Nr2kFilterSecurityInterceptor nr2kFilterSecurityInterceptor;
    @Autowired
    private UserDetailsService nr2kUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //user Details Service验证
        auth.userDetailsService(nr2kUserService).passwordEncoder(new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encode((String) rawPassword));
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .anyRequest().fullyAuthenticated() //任何请求,登录后可以访问
                .and()
                .formLogin()
                .loginPage("/admin/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index", true)
                .failureUrl("/admin/login")
                .permitAll() //登录页面用户任意访问
                .and()
                .logout().permitAll();//注销行为任意访问
        http.cors().disable();
        http.csrf().disable();
        http.sessionManagement().maximumSessions(1).expiredUrl("/admin/login");
        http.addFilterBefore(nr2kFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/fonts/**");
        web.ignoring().antMatchers("/downExample/**");
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/editor/**");
        web.ignoring().antMatchers("/files/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/pubtemplet/**");
        web.ignoring().antMatchers("/bootstrap*/**");
        web.ignoring().antMatchers("/static/**");
        web.ignoring().antMatchers("/images/**");
    }

    /**
     * 来自 http://blog.51cto.com/winters1224/2052034
     *
     * MyFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter
     * 1.继承AbstractSecurityInterceptor就会被加入springSecurityFilterChain
     * 2.实现Filter则加入了web应用的filter chain，
     * 3.springSecurityFilterChain也会被当成一个filter加入web应用的filter chain
     * 最终导致MyFilterSecurityInterceptor 被加载了两次
     * 这个配置就是让web应用的filter chain中的MyFilterSecurityInterceptor失效，只在spring security中起作用
     **/
    @Bean
    public FilterRegistrationBean registration(Nr2kFilterSecurityInterceptor filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SessionRegistry getSessionRegistry(){
        SessionRegistry sessionRegistry=new SessionRegistryImpl();
        return sessionRegistry;
    }
}
