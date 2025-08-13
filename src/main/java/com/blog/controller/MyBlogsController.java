package com.blog.controller;

import com.blog.config.SecurityUtils;
import com.blog.entity.Blog;
import com.blog.entity.User;
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
 * 我的博客管理控制器
 */
@Controller
public class MyBlogsController {

    private static final Logger logger = LoggerFactory.getLogger(MyBlogsController.class);

    @Autowired
    private BlogService blogService;

    /**
     * 我的博客管理页面
     */
    @GetMapping("/my-blogs")
    public String showMyBlogs(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.debug("显示我的博客页面，用户: {}, 页码: {}", currentUser.getUsername(), page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogService.getBlogsByAuthor(currentUser.getId(), pageable);

        model.addAttribute("blogPage", blogPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("totalElements", blogPage.getTotalElements());

        return "blog/my-blogs";
    }
}