package com.blog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 评论实体类
 */
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 多对一关系：多条评论属于一篇博客
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    // 多对一关系：多条评论属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 构造函数
    public Comment() {}

    public Comment(String content, Blog blog, User user) {
        this.content = content;
        this.blog = blog;
        this.user = user;
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

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // 便利方法
    public String getUserDisplayName() {
        if (user != null) {
            return user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();
        }
        return "匿名用户";
    }

    public String getBlogTitle() {
        return blog != null ? blog.getTitle() : "";
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", blog=" + (blog != null ? blog.getTitle() : null) +
                ", user=" + (user != null ? user.getUsername() : null) +
                '}';
    }
}