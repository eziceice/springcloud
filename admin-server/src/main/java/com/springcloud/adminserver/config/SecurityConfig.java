package com.springcloud.adminserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.formLogin().loginPage("/login.html").defaultSuccessUrl("/", true).permitAll();
        httpSecurity.logout().logoutUrl("/logout");
        httpSecurity.authorizeRequests().antMatchers("/login.html", "/**/**.css", "img/**", "/thrid-party/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/**").authenticated();
    }
}
