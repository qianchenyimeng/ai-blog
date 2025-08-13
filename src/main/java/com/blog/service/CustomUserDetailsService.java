package com.blog.service;

import com.blog.config.CustomUserDetails;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义用户详情服务，实现Spring Security的UserDetailsService接口
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     * 支持用户名或邮箱登录
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("尝试加载用户: {}", username);
        
        User user = null;
        
        // 首先尝试按用户名查找
        if (username.contains("@")) {
            // 如果包含@符号，按邮箱查找
            user = userRepository.findByEmail(username).orElse(null);
            logger.debug("按邮箱查找用户: {}", username);
        } else {
            // 否则按用户名查找
            user = userRepository.findByUsername(username).orElse(null);
            logger.debug("按用户名查找用户: {}", username);
        }
        
        // 如果第一次查找失败，尝试用另一种方式查找
        if (user == null) {
            user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
            logger.debug("按用户名或邮箱查找用户: {}", username);
        }
        
        if (user == null) {
            logger.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (!user.getEnabled()) {
            logger.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        logger.debug("成功加载用户: {} (ID: {})", user.getUsername(), user.getId());
        return new CustomUserDetails(user);
    }

    /**
     * 根据用户ID加载用户详情
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        logger.debug("尝试按ID加载用户: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在，ID: " + userId));
        
        if (!user.getEnabled()) {
            logger.warn("用户已被禁用，ID: {}", userId);
            throw new UsernameNotFoundException("用户已被禁用，ID: " + userId);
        }
        
        logger.debug("成功按ID加载用户: {} (ID: {})", user.getUsername(), user.getId());
        return new CustomUserDetails(user);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 验证用户凭据
     */
    public boolean validateUser(String username, String password) {
        try {
            UserDetails userDetails = loadUserByUsername(username);
            // 这里只是加载用户，密码验证由Spring Security的AuthenticationManager处理
            return userDetails != null && userDetails.isEnabled();
        } catch (UsernameNotFoundException e) {
            logger.debug("用户验证失败: {}", username);
            return false;
        }
    }
}