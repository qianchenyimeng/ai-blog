package com.blog.service;

import com.blog.dto.PasswordChangeDto;
import com.blog.dto.UserProfileDto;
import com.blog.dto.UserRegistrationDto;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户服务类
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        logger.info("开始注册用户: {}", registrationDto.getUsername());

        // 验证密码匹配
        if (!registrationDto.isPasswordMatching()) {
            throw new IllegalArgumentException("密码和确认密码不匹配");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + registrationDto.getUsername());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在: " + registrationDto.getEmail());
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setDisplayName(registrationDto.getDisplayName());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        logger.info("用户注册成功: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }

    /**
     * 根据ID获取用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱获取用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 更新用户资料
     */
    public User updateUserProfile(Long userId, UserProfileDto profileDto) {
        logger.info("开始更新用户资料: {} (ID: {})", profileDto.getUsername(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + userId));

        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(profileDto.getUsername()) &&
            userRepository.existsByUsernameAndIdNot(profileDto.getUsername(), userId)) {
            throw new IllegalArgumentException("用户名已被其他用户使用: " + profileDto.getUsername());
        }

        // 检查邮箱是否被其他用户使用
        if (!user.getEmail().equals(profileDto.getEmail()) &&
            userRepository.existsByEmailAndIdNot(profileDto.getEmail(), userId)) {
            throw new IllegalArgumentException("邮箱已被其他用户使用: " + profileDto.getEmail());
        }

        // 更新用户信息
        user.setUsername(profileDto.getUsername());
        user.setEmail(profileDto.getEmail());
        user.setDisplayName(profileDto.getDisplayName());

        User updatedUser = userRepository.save(user);
        logger.info("用户资料更新成功: {} (ID: {})", updatedUser.getUsername(), updatedUser.getId());

        return updatedUser;
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, PasswordChangeDto passwordChangeDto) {
        logger.info("开始修改密码，用户ID: {}", userId);

        // 验证新密码匹配
        if (!passwordChangeDto.isNewPasswordMatching()) {
            throw new IllegalArgumentException("新密码和确认密码不匹配");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + userId));

        // 验证当前密码
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("当前密码不正确");
        }

        // 检查新密码是否与当前密码相同
        if (passwordEncoder.matches(passwordChangeDto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与当前密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        logger.info("密码修改成功，用户ID: {}", userId);
    }

    /**
     * 检查用户名是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 启用/禁用用户
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        logger.info("设置用户状态，用户ID: {}, 启用: {}", userId, enabled);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + userId));

        user.setEnabled(enabled);
        userRepository.save(user);

        logger.info("用户状态设置成功，用户ID: {}, 启用: {}", userId, enabled);
    }

    /**
     * 将User实体转换为UserProfileDto
     */
    public UserProfileDto convertToProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    /**
     * 获取用户统计信息
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.countUsers();
    }

    /**
     * 获取启用用户数量
     */
    @Transactional(readOnly = true)
    public long getEnabledUserCount() {
        return userRepository.countEnabledUsers();
    }
}