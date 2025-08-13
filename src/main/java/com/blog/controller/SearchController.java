package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.service.BlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 搜索控制器
 */
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private BlogService blogService;

    /**
     * 博客搜索
     */
    @GetMapping("/search")
    public String searchBlogs(@RequestParam(value = "q", required = false) String keyword,
                             @RequestParam(value = "tag", required = false) String tag,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        
        logger.debug("搜索请求 - 关键词: {}, 标签: {}, 页码: {}", keyword, tag, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage;
        String searchType;
        String searchQuery;

        if (tag != null && !tag.trim().isEmpty()) {
            // 按标签搜索
            blogPage = blogService.getBlogsByTag(tag.trim(), pageable);
            searchType = "标签";
            searchQuery = tag.trim();
            logger.debug("按标签搜索: {}, 找到 {} 篇博客", tag, blogPage.getTotalElements());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索
            blogPage = blogService.searchBlogs(keyword.trim(), pageable);
            searchType = "关键词";
            searchQuery = keyword.trim();
            logger.debug("按关键词搜索: {}, 找到 {} 篇博客", keyword, blogPage.getTotalElements());
        } else {
            // 无搜索条件，显示所有博客
            blogPage = blogService.getAllPublishedBlogs(pageable);
            searchType = null;
            searchQuery = null;
            logger.debug("显示所有博客, 共 {} 篇", blogPage.getTotalElements());
        }

        // 添加模型属性
        model.addAttribute("blogPage", blogPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("totalElements", blogPage.getTotalElements());
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tag", tag);

        return "blog/search-results";
    }

    /**
     * 高级搜索页面
     */
    @GetMapping("/search/advanced")
    public String showAdvancedSearch(Model model) {
        // 可以添加一些统计信息，如热门标签等
        return "blog/advanced-search";
    }
}