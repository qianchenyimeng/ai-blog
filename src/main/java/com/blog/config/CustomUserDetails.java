package com.blog.config;

import com.blog.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户详情类，实现Spring Security的UserDetails接口
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 获取用户权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 目前所有用户都是普通用户角色
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 获取密码
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * 获取用户实体
     */
    public User getUser() {
        return user;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();
    }
}