package com.blog.util;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 */
public class XssUtils {

    private static final Logger logger = LoggerFactory.getLogger(XssUtils.class);

    // XSS攻击模式
    private static final Pattern[] XSS_PATTERNS = {
        // Script标签
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        
        // JavaScript事件
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        
        // 其他危险标签
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>.*?</embed>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<applet[^>]*>.*?</applet>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<meta[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<link[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<style[^>]*>.*?</style>", Pattern.CASE_INSENSITIVE),
        
        // 表达式
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 其他危险字符
        Pattern.compile("<\\s*\\w*\\s*(oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror=|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint|onafterupdate|onbefore|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)+\\s*=", Pattern.CASE_INSENSITIVE)
    };

    /**
     * 清理XSS攻击代码
     * @param value 原始值
     * @return 清理后的值
     */
    public static String cleanXSS(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            // HTML转义
            String cleanValue = StringEscapeUtils.escapeHtml4(value);
            
            // 移除危险模式
            for (Pattern pattern : XSS_PATTERNS) {
                cleanValue = pattern.matcher(cleanValue).replaceAll("");
            }
            
            // 移除空字符
            cleanValue = cleanValue.replaceAll("\0", "");
            
            // 如果内容被修改，记录日志
            if (!value.equals(cleanValue)) {
                logger.warn("检测到XSS攻击尝试，原始内容: {}, 清理后内容: {}", value, cleanValue);
            }
            
            return cleanValue;
            
        } catch (Exception e) {
            logger.error("XSS清理过程中发生错误", e);
            return StringEscapeUtils.escapeHtml4(value);
        }
    }

    /**
     * 检查是否包含XSS攻击代码
     * @param value 检查的值
     * @return 是否包含XSS攻击代码
     */
    public static boolean containsXSS(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 清理HTML标签，只保留安全的标签
     * @param html HTML内容
     * @return 清理后的HTML
     */
    public static String cleanHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        // 允许的安全标签
        String[] allowedTags = {"p", "br", "strong", "b", "em", "i", "u", "h1", "h2", "h3", "h4", "h5", "h6", 
                               "ul", "ol", "li", "blockquote", "pre", "code"};
        
        String cleanHtml = html;
        
        // 移除所有不安全的标签
        for (Pattern pattern : XSS_PATTERNS) {
            cleanHtml = pattern.matcher(cleanHtml).replaceAll("");
        }
        
        return cleanHtml;
    }

    /**
     * 转义SQL特殊字符，防止SQL注入
     * @param value 原始值
     * @return 转义后的值
     */
    public static String escapeSql(String value) {
        if (value == null) {
            return null;
        }
        
        return value.replaceAll("'", "''")
                   .replaceAll("\"", "\\\"")
                   .replaceAll("\\\\", "\\\\\\\\")
                   .replaceAll("%", "\\%")
                   .replaceAll("_", "\\_");
    }
}