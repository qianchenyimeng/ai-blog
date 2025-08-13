package com.blog.config;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据初始化器 - 仅在demo环境下运行
 */
@Component
@Profile("demo")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化演示数据...");

        // 创建用户
        User admin = createUser("admin", "admin@blog.com", "admin123", "系统管理员");
        User demoUser = createUser("demo_user", "demo@blog.com", "user123", "演示用户");

        // 创建标签
        Tag javaTag = createTag("Java");
        Tag springTag = createTag("Spring Boot");
        Tag webTag = createTag("Web开发");
        Tag techTag = createTag("技术分享");

        // 创建博客
        Blog welcomeBlog = createBlog(
            "欢迎使用个人博客系统",
            "这是一个基于Spring Boot开发的个人博客系统。\n\n" +
            "## 主要功能\n\n" +
            "- 用户注册和登录\n" +
            "- 博客文章管理\n" +
            "- 评论系统\n" +
            "- 标签分类\n" +
            "- 搜索功能\n\n" +
            "## 技术栈\n\n" +
            "- Spring Boot 2.7\n" +
            "- Spring Security\n" +
            "- Spring Data JPA\n" +
            "- H2 Database\n" +
            "- Thymeleaf\n" +
            "- Bootstrap\n\n" +
            "感谢使用本系统！",
            "这是一个基于Spring Boot开发的个人博客系统，包含用户管理、博客管理、评论系统等功能。",
            admin,
            new HashSet<>(Arrays.asList(javaTag, springTag, webTag, techTag))
        );

        Blog springBlog = createBlog(
            "Spring Boot 快速入门指南",
            "# Spring Boot 快速入门指南\n\n" +
            "Spring Boot是一个基于Spring框架的快速开发框架...\n\n" +
            "## 什么是Spring Boot\n\n" +
            "Spring Boot是Spring团队提供的全新框架，其设计目的是用来简化新Spring应用的初始搭建以及开发过程。\n\n" +
            "## 主要特性\n\n" +
            "1. 创建独立的Spring应用程序\n" +
            "2. 嵌入的Tomcat，无需部署WAR文件\n" +
            "3. 简化Maven配置\n" +
            "4. 自动配置Spring\n" +
            "5. 提供生产就绪型功能\n\n" +
            "## 快速开始\n\n" +
            "```java\n" +
            "@SpringBootApplication\n" +
            "public class Application {\n" +
            "    public static void main(String[] args) {\n" +
            "        SpringApplication.run(Application.class, args);\n" +
            "    }\n" +
            "}\n" +
            "```",
            "Spring Boot入门指南，介绍Spring Boot的基本概念和使用方法。",
            admin,
            new HashSet<>(Arrays.asList(javaTag, springTag, techTag))
        );

        Blog demoBlog = createBlog(
            "我的第一篇博客",
            "这是我在这个博客系统中发表的第一篇文章！\n\n" +
            "## 关于我\n\n" +
            "我是一个热爱技术的开发者，喜欢分享学习心得和技术经验。\n\n" +
            "## 博客计划\n\n" +
            "我计划在这里分享：\n" +
            "- 技术学习笔记\n" +
            "- 项目开发经验\n" +
            "- 生活感悟\n\n" +
            "欢迎大家关注和交流！",
            "演示用户的第一篇博客文章，介绍个人情况和博客计划。",
            demoUser,
            new HashSet<>(Arrays.asList(techTag))
        );

        // 创建评论
        createComment("很棒的博客系统，界面简洁，功能完善！", welcomeBlog, demoUser);
        createComment("感谢分享，对新手很有帮助。", springBlog, demoUser);
        createComment("欢迎加入我们的博客社区！", demoBlog, admin);

        logger.info("演示数据初始化完成！");
        logger.info("管理员账号: admin / admin123");
        logger.info("演示账号: demo_user / user123");
        logger.info("访问地址: http://localhost:8080");
        logger.info("H2控制台: http://localhost:8080/h2-console");
    }

    private User createUser(String username, String email, String password, String displayName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    private Blog createBlog(String title, String content, String summary, User author, Set<Tag> tags) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setSummary(summary);
        blog.setAuthor(author);
        blog.setPublished(true);
        blog.setViewCount(0L);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        
        // 先保存博客，再设置标签关系
        Blog savedBlog = blogRepository.save(blog);
        if (tags != null && !tags.isEmpty()) {
            savedBlog.setTags(new HashSet<>(tags));
            return blogRepository.save(savedBlog);
        }
        return savedBlog;
    }

    private Comment createComment(String content, Blog blog, User user) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setBlog(blog);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}