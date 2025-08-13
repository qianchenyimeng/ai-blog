package com.blog.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 自定义认证失败处理器
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = "登录失败，请检查用户名和密码";
        
        // 根据异常类型设置不同的错误消息
        if (exception instanceof BadCredentialsException) {
            errorMessage = "用户名或密码错误";
        } else if (exception instanceof DisabledException) {
            errorMessage = "账户已被禁用，请联系管理员";
        } else if (exception instanceof LockedException) {
            errorMessage = "账户已被锁定，请联系管理员";
        }
        
        // 获取用户输入的用户名，用于回显
        String username = request.getParameter("username");
        
        // 构建重定向URL，包含错误信息和用户名
        String redirectUrl = "/login?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8");
        if (username != null && !username.trim().isEmpty()) {
            redirectUrl += "&username=" + URLEncoder.encode(username, "UTF-8");
        }
        
        // 重定向到登录页面
        response.sendRedirect(redirectUrl);
    }
}