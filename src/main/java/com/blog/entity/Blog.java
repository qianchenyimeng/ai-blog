package com.blog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 博客实体类
 */
@Entity
@Table(name = "blogs")
public class Blog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "博客标题不能为空")
    @Size(min = 1, max = 200, message = "博客标题长度必须在1-200个字符之间")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "博客内容不能为空")
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Size(max = 500, message = "博客摘要长度不能超过500个字符")
    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "published", nullable = false)
    private Boolean published = true;

    // 多对一关系：多篇博客属于一个作者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // 一对多关系：一篇博客可以有多条评论
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    // 多对多关系：一篇博客可以有多个标签
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "blog_tags",
        joinColumns = @JoinColumn(name = "blog_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // 构造函数
    public Blog() {}

    public Blog(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
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

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // 便利方法
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBlog(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setBlog(null);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getBlogs().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getBlogs().remove(this);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    // 获取评论数量
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    // 生成摘要（如果没有手动设置）
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
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", viewCount=" + viewCount +
                ", published=" + published +
                ", author=" + (author != null ? author.getUsername() : null) +
                '}';
    }
}