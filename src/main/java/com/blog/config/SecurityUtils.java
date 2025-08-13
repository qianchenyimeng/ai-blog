package com.blog.config;

import com.blog.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前认证对象
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户详情
     */
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户实体
     */
    public static User getCurrentUser() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUser() : null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUserId() : null;
    }

    /**
     * 检查是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 检查当前用户是否为指定用户
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * 检查当前用户是否为指定用户
     */
    public static boolean isCurrentUser(String username) {
        String currentUsername = getCurrentUsername();
        return currentUsername != null && currentUsername.equals(username);
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(authority));
        }
        return false;
    }

    /**
     * 获取当前用户显示名称
     */
    public static String getCurrentUserDisplayName() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getDisplayName();
        }
        return getCurrentUsername();
    }
}