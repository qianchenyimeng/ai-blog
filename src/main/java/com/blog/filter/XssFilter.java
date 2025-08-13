package com.blog.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS攻击过滤器
 */
public class XssFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("XSS过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 包装请求，对参数进行XSS过滤
        XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(httpRequest);
        
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        logger.info("XSS过滤器销毁");
    }
}