package com.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 密码修改数据传输对象
 */
public class PasswordChangeDto {

    @NotBlank(message = "当前密码不能为空")
    private String currentPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度不能少于6个字符")
    private String newPassword;

    @NotBlank(message = "确认新密码不能为空")
    private String confirmNewPassword;

    // 构造函数
    public PasswordChangeDto() {}

    public PasswordChangeDto(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    /**
     * 验证新密码是否匹配
     */
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    @Override
    public String toString() {
        return "PasswordChangeDto{" +
                "currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmNewPassword='[PROTECTED]'" +
                '}';
    }
}