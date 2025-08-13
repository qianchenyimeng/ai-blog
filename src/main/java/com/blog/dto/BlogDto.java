package com.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 博客数据传输对象
 */
public class BlogDto {

    private Long id;

    @NotBlank(message = "博客标题不能为空")
    @Size(min = 1, max = 200, message = "博客标题长度必须在1-200个字符之间")
    private String title;

    @NotBlank(message = "博客内容不能为空")
    private String content;

    @Size(max = 500, message = "博客摘要长度不能超过500个字符")
    private String summary;

    private Boolean published = true;

    private String tags; // 标签字符串，用逗号分隔

    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long viewCount;
    private Integer commentCount;

    // 构造函数
    public BlogDto() {}

    public BlogDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * 获取自动生成的摘要
     */
    public String getAutoSummary() {
        if (summary != null && !summary.trim().isEmpty()) {
            return summary;
        }
        if (content != null && content.length() > 200) {
            return content.substring(0, 200) + "...";
        }
        return content;
    }

    @Override
    public String toString() {
        return "BlogDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", authorName='" + authorName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}