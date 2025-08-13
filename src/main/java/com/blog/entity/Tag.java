package com.blog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * 标签实体类
 */
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 50, message = "标签名称长度必须在1-50个字符之间")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    // 多对多关系：标签可以被多篇博客使用
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Blog> blogs = new HashSet<>();

    // 构造函数
    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(Set<Blog> blogs) {
        this.blogs = blogs;
    }

    // 便利方法
    public void addBlog(Blog blog) {
        blogs.add(blog);
        blog.getTags().add(this);
    }

    public void removeBlog(Blog blog) {
        blogs.remove(blog);
        blog.getTags().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return name != null && name.equals(tag.getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}