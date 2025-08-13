package com.blog.controller;

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

import com.blog.dto.BlogDto;

/**
 * 主页控制器
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BlogService blogService;

    /**
     * 主页 - 显示最新博客列表
     */
    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {
        try {
            logger.debug("访问主页，页码: {}, 大小: {}", page, size);
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size);
            
            // 获取已发布的博客列表
            Page<com.blog.entity.Blog> blogEntityPage = blogService.getAllPublishedBlogs(pageable);
            
            // 转换为DTO
            Page<BlogDto> blogPage = blogEntityPage.map(blog -> blogService.convertToBlogDto(blog));
            
            // 添加到模型
            model.addAttribute("blogPage", blogPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", blogPage.getTotalPages());
            model.addAttribute("totalElements", blogPage.getTotalElements());
            
            logger.debug("主页数据加载完成，博客数量: {}", blogPage.getTotalElements());
            
            return "index";
        } catch (Exception e) {
            logger.error("加载主页时发生错误", e);
            model.addAttribute("errorMessage", "加载页面时发生错误，请稍后重试");
            return "index";
        }
    }
}