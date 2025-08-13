package com.blog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * SQL注入防护工具类
 */
public class SqlInjectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlInjectionUtils.class);

    // SQL注入攻击模式
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        // 常见SQL注入关键词
        Pattern.compile("(?i).*\\b(select|insert|update|delete|drop|create|alter|exec|execute|union|script|declare|cast|convert)\\b.*"),
        
        // SQL注释
        Pattern.compile("(?i).*(--|#|/\\*|\\*/).*"),
        
        // 单引号和双引号注入
        Pattern.compile("(?i).*('|(\\\\x27)|(\\\\x2D\\\\x2D)).*"),
        
        // 十六进制编码
        Pattern.compile("(?i).*(\\\\x[0-9a-f]{2}).*"),
        
        // 常见注入尝试
        Pattern.compile("(?i).*(or\\s+1\\s*=\\s*1|and\\s+1\\s*=\\s*1).*"),
        Pattern.compile("(?i).*(or\\s+'1'\\s*=\\s*'1'|and\\s+'1'\\s*=\\s*'1').*"),
        Pattern.compile("(?i).*(or\\s+\"1\"\\s*=\\s*\"1\"|and\\s+\"1\"\\s*=\\s*\"1\").*"),
        
        // 时间延迟注入
        Pattern.compile("(?i).*(waitfor\\s+delay|sleep\\s*\\(|benchmark\\s*\\().*"),
        
        // 联合查询注入
        Pattern.compile("(?i).*(union\\s+(all\\s+)?select).*"),
        
        // 子查询注入
        Pattern.compile("(?i).*(\\(\\s*select\\s+.+\\s+from\\s+.+\\)).*"),
        
        // 存储过程调用
        Pattern.compile("(?i).*(exec\\s*\\(|sp_|xp_).*"),
        
        // 数据库函数
        Pattern.compile("(?i).*(user\\s*\\(|database\\s*\\(|version\\s*\\(|@@version|@@user).*")
    };

    /**
     * 检查是否包含SQL注入攻击代码
     * @param value 检查的值
     * @return 是否包含SQL注入攻击代码
     */
    public static boolean containsSqlInjection(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String cleanValue = value.trim().toLowerCase();
        
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(cleanValue).matches()) {
                logger.warn("检测到SQL注入攻击尝试: {}", value);
                return true;
            }
        }
        
        return false;
    }

    /**
     * 清理SQL注入攻击代码
     * @param value 原始值
     * @return 清理后的值
     */
    public static String cleanSqlInjection(String value) {
        if (value == null) {
            return null;
        }

        if (containsSqlInjection(value)) {
            logger.warn("检测到并清理SQL注入攻击尝试: {}", value);
            // 移除危险字符
            return value.replaceAll("[';\"\\-\\-#/\\*\\*/]", "")
                       .replaceAll("(?i)\\b(select|insert|update|delete|drop|create|alter|exec|execute|union|script|declare|cast|convert)\\b", "")
                       .trim();
        }
        
        return value;
    }

    /**
     * 验证搜索关键词的安全性
     * @param keyword 搜索关键词
     * @return 是否安全
     */
    public static boolean isSearchKeywordSafe(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        // 检查长度
        if (keyword.length() > 100) {
            logger.warn("搜索关键词过长: {}", keyword);
            return false;
        }

        // 检查SQL注入
        if (containsSqlInjection(keyword)) {
            return false;
        }

        // 检查特殊字符
        if (keyword.matches(".*[<>\"'%;()&+\\-].*")) {
            logger.warn("搜索关键词包含特殊字符: {}", keyword);
            return false;
        }

        return true;
    }

    /**
     * 清理搜索关键词
     * @param keyword 原始关键词
     * @return 清理后的关键词
     */
    public static String cleanSearchKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }

        // 移除危险字符
        String cleanKeyword = keyword.replaceAll("[<>\"'%;()&+\\-]", "")
                                    .replaceAll("\\s+", " ")
                                    .trim();

        // 限制长度
        if (cleanKeyword.length() > 100) {
            cleanKeyword = cleanKeyword.substring(0, 100);
        }

        return cleanKeyword;
    }

    /**
     * 验证排序参数的安全性
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 是否安全
     */
    public static boolean isSortParameterSafe(String sortBy, String sortDir) {
        // 允许的排序字段
        String[] allowedSortFields = {"id", "title", "createdAt", "updatedAt", "viewCount", "username", "email"};
        
        // 允许的排序方向
        String[] allowedSortDirections = {"asc", "desc", "ASC", "DESC"};

        if (sortBy != null) {
            boolean validSortBy = false;
            for (String field : allowedSortFields) {
                if (field.equals(sortBy)) {
                    validSortBy = true;
                    break;
                }
            }
            if (!validSortBy) {
                logger.warn("无效的排序字段: {}", sortBy);
                return false;
            }
        }

        if (sortDir != null) {
            boolean validSortDir = false;
            for (String dir : allowedSortDirections) {
                if (dir.equals(sortDir)) {
                    validSortDir = true;
                    break;
                }
            }
            if (!validSortDir) {
                logger.warn("无效的排序方向: {}", sortDir);
                return false;
            }
        }

        return true;
    }
}