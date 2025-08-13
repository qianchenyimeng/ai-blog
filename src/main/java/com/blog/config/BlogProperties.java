package com.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 博客系统自定义配置属性
 */
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogProperties {

    private Upload upload = new Upload();
    private Pagination pagination = new Pagination();
    private Cache cache = new Cache();
    private Security security = new Security();
    private Notification notification = new Notification();
    private Search search = new Search();
    private Comment comment = new Comment();
    private BlogPost blogPost = new BlogPost();

    // Getters and Setters
    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public BlogPost getBlogPost() {
        return blogPost;
    }

    public void setBlogPost(BlogPost blogPost) {
        this.blogPost = blogPost;
    }

    /**
     * 文件上传配置
     */
    public static class Upload {
        private String path = "uploads";
        private String maxFileSize = "10MB";
        private String allowedTypes = "jpg,jpeg,png,gif,pdf,doc,docx";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(String maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String getAllowedTypes() {
            return allowedTypes;
        }

        public void setAllowedTypes(String allowedTypes) {
            this.allowedTypes = allowedTypes;
        }

        public String[] getAllowedTypesArray() {
            return allowedTypes.split(",");
        }
    }

    /**
     * 分页配置
     */
    public static class Pagination {
        private int defaultPageSize = 10;
        private int maxPageSize = 100;

        public int getDefaultPageSize() {
            return defaultPageSize;
        }

        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }

        public int getMaxPageSize() {
            return maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
    }

    /**
     * 缓存配置
     */
    public static class Cache {
        private int blogListTtl = 300;
        private int userProfileTtl = 600;
        private int tagCloudTtl = 1800;

        public int getBlogListTtl() {
            return blogListTtl;
        }

        public void setBlogListTtl(int blogListTtl) {
            this.blogListTtl = blogListTtl;
        }

        public int getUserProfileTtl() {
            return userProfileTtl;
        }

        public void setUserProfileTtl(int userProfileTtl) {
            this.userProfileTtl = userProfileTtl;
        }

        public int getTagCloudTtl() {
            return tagCloudTtl;
        }

        public void setTagCloudTtl(int tagCloudTtl) {
            this.tagCloudTtl = tagCloudTtl;
        }
    }

    /**
     * 安全配置
     */
    public static class Security {
        private String passwordStrength = "medium";
        private int sessionTimeout = 1800;
        private int maxLoginAttempts = 5;
        private int lockoutDuration = 900;

        public String getPasswordStrength() {
            return passwordStrength;
        }

        public void setPasswordStrength(String passwordStrength) {
            this.passwordStrength = passwordStrength;
        }

        public int getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }

        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }

        public int getLockoutDuration() {
            return lockoutDuration;
        }

        public void setLockoutDuration(int lockoutDuration) {
            this.lockoutDuration = lockoutDuration;
        }
    }

    /**
     * 通知配置
     */
    public static class Notification {
        private boolean enabled = false;
        private String fromEmail = "noreply@blog.com";
        private String adminEmail = "admin@blog.com";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getFromEmail() {
            return fromEmail;
        }

        public void setFromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
        }

        public String getAdminEmail() {
            return adminEmail;
        }

        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }
    }

    /**
     * 搜索配置
     */
    public static class Search {
        private int maxResults = 100;
        private boolean highlightEnabled = true;

        public int getMaxResults() {
            return maxResults;
        }

        public void setMaxResults(int maxResults) {
            this.maxResults = maxResults;
        }

        public boolean isHighlightEnabled() {
            return highlightEnabled;
        }

        public void setHighlightEnabled(boolean highlightEnabled) {
            this.highlightEnabled = highlightEnabled;
        }
    }

    /**
     * 评论配置
     */
    public static class Comment {
        private boolean moderationEnabled = false;
        private int maxLength = 1000;
        private int rateLimit = 10;

        public boolean isModerationEnabled() {
            return moderationEnabled;
        }

        public void setModerationEnabled(boolean moderationEnabled) {
            this.moderationEnabled = moderationEnabled;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        public int getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }
    }

    /**
     * 博客文章配置
     */
    public static class BlogPost {
        private int maxTitleLength = 200;
        private int maxContentLength = 50000;
        private int autoSaveInterval = 30;

        public int getMaxTitleLength() {
            return maxTitleLength;
        }

        public void setMaxTitleLength(int maxTitleLength) {
            this.maxTitleLength = maxTitleLength;
        }

        public int getMaxContentLength() {
            return maxContentLength;
        }

        public void setMaxContentLength(int maxContentLength) {
            this.maxContentLength = maxContentLength;
        }

        public int getAutoSaveInterval() {
            return autoSaveInterval;
        }

        public void setAutoSaveInterval(int autoSaveInterval) {
            this.autoSaveInterval = autoSaveInterval;
        }
    }
}