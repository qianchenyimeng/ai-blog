package com.blog.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证成功处理器
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        // 获取登录前用户想要访问的URL
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        
        String targetUrl = "/";
        
        if (savedRequest != null) {
            targetUrl = savedRequest.getRedirectUrl();
        } else {
            // 如果没有保存的请求，根据用户角色决定跳转页面
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.contains("/login")) {
                targetUrl = referer;
            }
        }
        
        // 清除保存的请求
        requestCache.removeRequest(request, response);
        
        // 添加成功消息
        request.getSession().setAttribute("successMessage", "登录成功！欢迎回来，" + authentication.getName());
        
        // 重定向到目标URL
        response.sendRedirect(targetUrl);
    }
}