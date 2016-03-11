package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author volodymyr.tsukur
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return new CustomUserDetails(username);
        }

        public static class CustomUserDetails implements UserDetails {

            private final SimpleGrantedAuthority USER_ROLE = new SimpleGrantedAuthority("ROLE_USER");

            private final SimpleGrantedAuthority USER_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");

            private final Collection<? extends GrantedAuthority> ROLES_USER =
                    Collections.singletonList(USER_ROLE);

            private final Collection<? extends GrantedAuthority> ROLES_USER_AND_ADMIN =
                    Arrays.asList(USER_ROLE, USER_ADMIN);

            private final String username;

            private final Collection<? extends GrantedAuthority> roles;

            public CustomUserDetails(final String username) {
                this.username = username;
                roles = "hontareva".equals(username) ? ROLES_USER_AND_ADMIN : ROLES_USER;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return roles;
            }

            @Override
            public String getPassword() {
                return "123";
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

        }

    }

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/ads/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/ads/**").hasRole("USER")
                .antMatchers(HttpMethod.PATCH, "/ads/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/ads/**").hasRole("USER")
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    public static CustomUserDetailsService.CustomUserDetails currentPrincipal() {
        return (CustomUserDetailsService.CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
