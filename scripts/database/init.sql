-- 个人博客系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS personal_blog 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE personal_blog;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_enabled (enabled),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建博客表
CREATE TABLE IF NOT EXISTS blogs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(500),
    view_count BIGINT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_title (title),
    INDEX idx_author_id (author_id),
    INDEX idx_published (published),
    INDEX idx_created_at (created_at),
    INDEX idx_view_count (view_count),
    FULLTEXT idx_content (title, content, summary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建博客标签关联表
CREATE TABLE IF NOT EXISTS blog_tags (
    blog_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (blog_id, tag_id),
    FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    INDEX idx_blog_id (blog_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    blog_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_blog_id (blog_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例数据
-- 创建管理员用户 (密码: admin123)
INSERT INTO users (username, email, password, display_name, enabled) VALUES 
('admin', 'admin@blog.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyR5HjJixjy', '系统管理员', TRUE);

-- 创建示例用户 (密码: user123)
INSERT INTO users (username, email, password, display_name, enabled) VALUES 
('demo_user', 'demo@blog.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '演示用户', TRUE);

-- 创建示例标签
INSERT INTO tags (name) VALUES 
('Java'), ('Spring Boot'), ('MySQL'), ('Web开发'), ('后端开发'), 
('前端开发'), ('JavaScript'), ('HTML'), ('CSS'), ('技术分享');

-- 创建示例博客
INSERT INTO blogs (title, content, summary, author_id, published, view_count) VALUES 
('欢迎使用个人博客系统', 
 '这是一个基于Spring Boot开发的个人博客系统。\n\n## 主要功能\n\n- 用户注册和登录\n- 博客文章管理\n- 评论系统\n- 标签分类\n- 搜索功能\n\n## 技术栈\n\n- Spring Boot 2.7\n- Spring Security\n- Spring Data JPA\n- MySQL\n- Thymeleaf\n- Bootstrap\n\n感谢使用本系统！', 
 '这是一个基于Spring Boot开发的个人博客系统，包含用户管理、博客管理、评论系统等功能。', 
 1, TRUE, 0),

('Spring Boot 入门指南', 
 '# Spring Boot 入门指南\n\nSpring Boot是一个基于Spring框架的快速开发框架...\n\n## 什么是Spring Boot\n\nSpring Boot是Spring团队提供的全新框架，其设计目的是用来简化新Spring应用的初始搭建以及开发过程。\n\n## 主要特性\n\n1. 创建独立的Spring应用程序\n2. 嵌入的Tomcat，无需部署WAR文件\n3. 简化Maven配置\n4. 自动配置Spring\n5. 提供生产就绪型功能\n\n## 快速开始\n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```', 
 'Spring Boot入门指南，介绍Spring Boot的基本概念和使用方法。', 
 1, TRUE, 0);

-- 为博客添加标签
INSERT INTO blog_tags (blog_id, tag_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 10);

-- 创建示例评论
INSERT INTO comments (content, blog_id, user_id) VALUES 
('很棒的博客系统，界面简洁，功能完善！', 1, 2),
('感谢分享，对新手很有帮助。', 2, 2);

-- 创建数据库用户（生产环境使用）
-- CREATE USER 'blog_user'@'localhost' IDENTIFIED BY 'your_secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON personal_blog.* TO 'blog_user'@'localhost';
-- FLUSH PRIVILEGES;