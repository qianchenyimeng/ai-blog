package com.blog.controller;

import com.blog.config.SecurityUtils;
import com.blog.dto.BlogDto;
import com.blog.dto.CommentDto;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 博客控制器
 */
@Controller
@RequestMapping("/blog")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * 显示博客创建页面
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("blogDto", new BlogDto());
        model.addAttribute("isEdit", false);
        return "blog/create-blog";
    }

    /**
     * 处理博客创建
     */
    @PostMapping("/create")
    public String createBlog(@Valid @ModelAttribute("blogDto") BlogDto blogDto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理博客创建请求: {}, 作者: {}", blogDto.getTitle(), currentUser.getUsername());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("博客创建表单验证失败: {}", bindingResult.getAllErrors());
            return "blog/create-blog";
        }

        try {
            // 创建博客
            Blog blog = blogService.createBlog(blogDto, currentUser.getId());
            
            logger.info("博客创建成功: {} (ID: {})", blog.getTitle(), blog.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                "博客《" + blog.getTitle() + "》创建成功！");
            
            return "redirect:/blog/" + blog.getId();
            
        } catch (IllegalArgumentException e) {
            logger.warn("博客创建失败: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "blog/create-blog";
            
        } catch (Exception e) {
            logger.error("博客创建时发生未知错误", e);
            model.addAttribute("errorMessage", "博客创建失败，请稍后重试");
            return "blog/create-blog";
        }
    }

    /**
     * 显示博客详情页面
     */
    @GetMapping("/{id}")
    public String showBlogDetail(@PathVariable Long id, Model model) {
        logger.debug("显示博客详情，ID: {}", id);

        Optional<Blog> blogOpt = blogService.getBlogByIdAndIncrementView(id);
        if (!blogOpt.isPresent()) {
            logger.warn("博客不存在，ID: {}", id);
            model.addAttribute("errorMessage", "博客不存在");
            return "error/404";
        }

        Blog blog = blogOpt.get();
        
        // 检查博客是否已发布或用户是否为作者
        User currentUser = SecurityUtils.getCurrentUser();
        boolean canView = blog.getPublished() || 
                         (currentUser != null && currentUser.getId().equals(blog.getAuthor().getId()));
        
        if (!canView) {
            logger.warn("用户无权查看未发布的博客，ID: {}", id);
            model.addAttribute("errorMessage", "博客不存在或未发布");
            return "error/403";
        }

        BlogDto blogDto = blogService.convertToBlogDto(blog);
        model.addAttribute("blog", blogDto);
        
        // 检查当前用户是否为作者
        boolean isAuthor = currentUser != null && currentUser.getId().equals(blog.getAuthor().getId());
        model.addAttribute("isAuthor", isAuthor);

        // 获取评论列表
        List<CommentDto> comments = commentService.getCommentsByBlogId(id);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", comments.size());

        // 为登录用户添加评论表单
        if (currentUser != null) {
            model.addAttribute("commentDto", new CommentDto());
        }

        return "blog/blog-detail";
    }

    /**
     * 显示博客编辑页面
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.debug("显示博客编辑页面，ID: {}, 用户: {}", id, currentUser.getUsername());

        Optional<Blog> blogOpt = blogService.getBlogById(id);
        if (!blogOpt.isPresent()) {
            logger.warn("博客不存在，ID: {}", id);
            model.addAttribute("errorMessage", "博客不存在");
            return "error/404";
        }

        Blog blog = blogOpt.get();
        
        // 检查用户是否为作者
        if (!currentUser.getId().equals(blog.getAuthor().getId())) {
            logger.warn("用户无权编辑博客，博客ID: {}, 用户: {}", id, currentUser.getUsername());
            model.addAttribute("errorMessage", "您没有权限编辑此博客");
            return "error/403";
        }

        BlogDto blogDto = blogService.convertToBlogDto(blog);
        model.addAttribute("blogDto", blogDto);
        model.addAttribute("isEdit", true);

        return "blog/create-blog";
    }

    /**
     * 处理博客更新
     */
    @PostMapping("/{id}/edit")
    public String updateBlog(@PathVariable Long id,
                            @Valid @ModelAttribute("blogDto") BlogDto blogDto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理博客更新请求，ID: {}, 标题: {}, 用户: {}", id, blogDto.getTitle(), currentUser.getUsername());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("博客更新表单验证失败: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", true);
            return "blog/create-blog";
        }

        try {
            // 更新博客
            Blog blog = blogService.updateBlog(id, blogDto, currentUser.getId());
            
            logger.info("博客更新成功: {} (ID: {})", blog.getTitle(), blog.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                "博客《" + blog.getTitle() + "》更新成功！");
            
            return "redirect:/blog/" + blog.getId();
            
        } catch (IllegalArgumentException e) {
            logger.warn("博客更新失败: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            return "blog/create-blog";
            
        } catch (Exception e) {
            logger.error("博客更新时发生未知错误", e);
            model.addAttribute("errorMessage", "博客更新失败，请稍后重试");
            model.addAttribute("isEdit", true);
            return "blog/create-blog";
        }
    }

    /**
     * 删除博客
     */
    @PostMapping("/{id}/delete")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理博客删除请求，ID: {}, 用户: {}", id, currentUser.getUsername());

        try {
            blogService.deleteBlog(id, currentUser.getId());
            
            logger.info("博客删除成功，ID: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "博客删除成功！");
            
            return "redirect:/my-blogs";
            
        } catch (IllegalArgumentException e) {
            logger.warn("博客删除失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/blog/" + id;
            
        } catch (Exception e) {
            logger.error("博客删除时发生未知错误", e);
            redirectAttributes.addFlashAttribute("errorMessage", "博客删除失败，请稍后重试");
            return "redirect:/blog/" + id;
        }
    }


}