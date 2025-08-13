package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security 安全配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 会话注册表
     */
    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }

    /**
     * 配置认证管理器
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * 配置HTTP安全
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 授权配置
            .authorizeRequests()
                // 公开访问的路径
                .antMatchers("/", "/home", "/index").permitAll()
                .antMatchers("/blog/**").permitAll()
                .antMatchers("/search/**").permitAll()
                .antMatchers("/register", "/login").permitAll()
                .antMatchers("/error/**").permitAll()
                // 静态资源
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                // H2控制台（仅开发环境）
                .antMatchers("/h2-console/**").permitAll()
                // 需要认证的路径
                .antMatchers("/my-blogs/**", "/profile/**").authenticated()
                .antMatchers("/blog/create/**", "/blog/edit/**", "/blog/delete/**").authenticated()
                .antMatchers("/comment/**").authenticated()
                // 其他请求需要认证
                .anyRequest().authenticated()
            .and()
            
            // 表单登录配置
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
            .and()
            
            // 注销配置
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            .and()
            
            // 记住我功能
            .rememberMe()
                .key("blog-remember-me-key")
                .tokenValiditySeconds(86400 * 7) // 7天
                .userDetailsService(userDetailsService)
            .and()
            
            // 会话管理
            .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry())
                .and()
            .and()
            
            // CSRF配置
            .csrf()
                .ignoringAntMatchers("/h2-console/**")
            .and()
            
            // 安全头部配置
            .headers()
                .frameOptions().sameOrigin();
    }
}