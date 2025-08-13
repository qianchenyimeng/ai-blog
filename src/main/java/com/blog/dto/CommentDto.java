package com.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 */
public class CommentDto {

    private Long id;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    private String content;

    private Long blogId;
    private String blogTitle;
    private Long userId;
    private String userName;
    private String userDisplayName;
    private LocalDateTime createdAt;

    // 构造函数
    public CommentDto() {}

    public CommentDto(String content) {
        this.content = content;
    }

    public CommentDto(Long id, String content, Long blogId, String blogTitle, 
                     Long userId, String userName, String userDisplayName, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取用户显示名称（优先显示displayName，否则显示username）
     */
    public String getDisplayName() {
        return userDisplayName != null && !userDisplayName.trim().isEmpty() 
               ? userDisplayName : userName;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", blogId=" + blogId +
                ", userName='" + userName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}