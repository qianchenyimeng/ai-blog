package com.blog.controller;

import com.blog.config.SecurityUtils;
import com.blog.dto.PasswordChangeDto;
import com.blog.dto.UserProfileDto;
import com.blog.dto.UserRegistrationDto;
import com.blog.entity.User;
import com.blog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "user/register";
    }

    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistration") UserRegistrationDto registrationDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        logger.info("处理用户注册请求: {}", registrationDto.getUsername());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("注册表单验证失败: {}", bindingResult.getAllErrors());
            return "user/register";
        }

        // 验证密码匹配
        if (!registrationDto.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "密码和确认密码不匹配");
            return "user/register";
        }

        try {
            // 注册用户
            User user = userService.registerUser(registrationDto);
            
            logger.info("用户注册成功: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", 
                "注册成功！欢迎加入博客系统，" + user.getUsername() + "！请登录开始使用。");
            
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            logger.warn("用户注册失败: {}", e.getMessage());
            
            // 根据错误类型设置具体的错误信息
            if (e.getMessage().contains("用户名已存在")) {
                bindingResult.rejectValue("username", "error.username", e.getMessage());
            } else if (e.getMessage().contains("邮箱已存在")) {
                bindingResult.rejectValue("email", "error.email", e.getMessage());
            } else {
                model.addAttribute("errorMessage", e.getMessage());
            }
            
            return "user/register";
        } catch (Exception e) {
            logger.error("用户注册时发生未知错误", e);
            model.addAttribute("errorMessage", "注册失败，请稍后重试");
            return "user/register";
        }
    }

    /**
     * 显示用户资料页面
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        UserProfileDto profileDto = userService.convertToProfileDto(currentUser);
        model.addAttribute("userProfile", profileDto);
        model.addAttribute("passwordChange", new PasswordChangeDto());
        
        return "user/profile";
    }

    /**
     * 更新用户资料
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDto profileDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理用户资料更新请求: {}", profileDto.getUsername());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("资料更新表单验证失败: {}", bindingResult.getAllErrors());
            model.addAttribute("passwordChange", new PasswordChangeDto());
            return "user/profile";
        }

        try {
            // 更新用户资料
            User updatedUser = userService.updateUserProfile(currentUser.getId(), profileDto);
            
            logger.info("用户资料更新成功: {}", updatedUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "资料更新成功！");
            
            return "redirect:/profile";
            
        } catch (IllegalArgumentException e) {
            logger.warn("用户资料更新失败: {}", e.getMessage());
            
            // 根据错误类型设置具体的错误信息
            if (e.getMessage().contains("用户名已被其他用户使用")) {
                bindingResult.rejectValue("username", "error.username", e.getMessage());
            } else if (e.getMessage().contains("邮箱已被其他用户使用")) {
                bindingResult.rejectValue("email", "error.email", e.getMessage());
            } else {
                model.addAttribute("errorMessage", e.getMessage());
            }
            
            model.addAttribute("passwordChange", new PasswordChangeDto());
            return "user/profile";
            
        } catch (Exception e) {
            logger.error("用户资料更新时发生未知错误", e);
            model.addAttribute("errorMessage", "资料更新失败，请稍后重试");
            model.addAttribute("passwordChange", new PasswordChangeDto());
            return "user/profile";
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordChange") PasswordChangeDto passwordChangeDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理密码修改请求，用户ID: {}", currentUser.getId());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("密码修改表单验证失败: {}", bindingResult.getAllErrors());
            UserProfileDto profileDto = userService.convertToProfileDto(currentUser);
            model.addAttribute("userProfile", profileDto);
            return "user/profile";
        }

        // 验证新密码匹配
        if (!passwordChangeDto.isNewPasswordMatching()) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "新密码和确认密码不匹配");
            UserProfileDto profileDto = userService.convertToProfileDto(currentUser);
            model.addAttribute("userProfile", profileDto);
            return "user/profile";
        }

        try {
            // 修改密码
            userService.changePassword(currentUser.getId(), passwordChangeDto);
            
            logger.info("密码修改成功，用户ID: {}", currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "密码修改成功！");
            
            return "redirect:/profile";
            
        } catch (IllegalArgumentException e) {
            logger.warn("密码修改失败: {}", e.getMessage());
            
            // 根据错误类型设置具体的错误信息
            if (e.getMessage().contains("当前密码不正确")) {
                bindingResult.rejectValue("currentPassword", "error.currentPassword", e.getMessage());
            } else if (e.getMessage().contains("新密码不能与当前密码相同")) {
                bindingResult.rejectValue("newPassword", "error.newPassword", e.getMessage());
            } else {
                model.addAttribute("passwordErrorMessage", e.getMessage());
            }
            
            UserProfileDto profileDto = userService.convertToProfileDto(currentUser);
            model.addAttribute("userProfile", profileDto);
            return "user/profile";
            
        } catch (Exception e) {
            logger.error("密码修改时发生未知错误", e);
            model.addAttribute("passwordErrorMessage", "密码修改失败，请稍后重试");
            UserProfileDto profileDto = userService.convertToProfileDto(currentUser);
            model.addAttribute("userProfile", profileDto);
            return "user/profile";
        }
    }

    /**
     * AJAX检查用户名是否可用
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // 如果是当前用户的用户名，则可用
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            return true;
        }
        
        return !userService.existsByUsername(username);
    }

    /**
     * AJAX检查邮箱是否可用
     */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // 如果是当前用户的邮箱，则可用
        if (currentUser != null && currentUser.getEmail().equals(email)) {
            return true;
        }
        
        return !userService.existsByEmail(email);
    }
}