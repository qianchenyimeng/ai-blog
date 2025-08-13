package com.blog.controller;

import com.blog.config.SecurityUtils;
import com.blog.dto.CommentDto;
import com.blog.entity.Comment;
import com.blog.entity.User;
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

/**
 * 评论控制器
 */
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     */
    @PostMapping("/blog/{blogId}/comment")
    public String addComment(@PathVariable Long blogId,
                            @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理评论添加请求，博客ID: {}, 用户: {}", blogId, currentUser.getUsername());

        // 验证表单数据
        if (bindingResult.hasErrors()) {
            logger.warn("评论表单验证失败: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("commentError", "评论内容不能为空或过长");
            return "redirect:/blog/" + blogId;
        }

        try {
            // 添加评论
            Comment comment = commentService.addComment(blogId, commentDto, currentUser.getId());
            
            logger.info("评论添加成功，ID: {}", comment.getId());
            redirectAttributes.addFlashAttribute("successMessage", "评论发表成功！");
            
        } catch (IllegalArgumentException e) {
            logger.warn("评论添加失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            
        } catch (Exception e) {
            logger.error("评论添加时发生未知错误", e);
            redirectAttributes.addFlashAttribute("errorMessage", "评论发表失败，请稍后重试");
        }

        return "redirect:/blog/" + blogId;
    }

    /**
     * 删除评论
     */
    @PostMapping("/comment/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                               @RequestParam Long blogId,
                               RedirectAttributes redirectAttributes) {
        
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.info("处理评论删除请求，评论ID: {}, 用户: {}", commentId, currentUser.getUsername());

        try {
            // 删除评论
            commentService.deleteComment(commentId, currentUser.getId());
            
            logger.info("评论删除成功，ID: {}", commentId);
            redirectAttributes.addFlashAttribute("successMessage", "评论删除成功！");
            
        } catch (IllegalArgumentException e) {
            logger.warn("评论删除失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            
        } catch (Exception e) {
            logger.error("评论删除时发生未知错误", e);
            redirectAttributes.addFlashAttribute("errorMessage", "评论删除失败，请稍后重试");
        }

        return "redirect:/blog/" + blogId;
    }

    /**
     * 我的评论管理页面
     */
    @GetMapping("/my-comments")
    public String showMyComments(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.debug("显示我的评论页面，用户: {}, 页码: {}", currentUser.getUsername(), page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentService.getCommentsByUser(currentUser.getId(), pageable);

        model.addAttribute("commentPage", commentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("totalElements", commentPage.getTotalElements());

        return "comment/my-comments";
    }

    /**
     * 博客评论管理页面（博客作者查看和管理自己博客的评论）
     */
    @GetMapping("/blog/{blogId}/comments/manage")
    public String manageBlogComments(@PathVariable Long blogId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        logger.debug("显示博客评论管理页面，博客ID: {}, 用户: {}", blogId, currentUser.getUsername());

        try {
            List<CommentDto> comments = commentService.getCommentsByBlogAndAuthor(blogId, currentUser.getId());
            
            model.addAttribute("comments", comments);
            model.addAttribute("blogId", blogId);
            
            return "comment/manage-comments";
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取博客评论失败: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/403";
        }
    }

    /**
     * AJAX获取博客评论列表
     */
    @GetMapping("/blog/{blogId}/comments")
    @ResponseBody
    public List<CommentDto> getBlogComments(@PathVariable Long blogId) {
        logger.debug("获取博客评论列表，博客ID: {}", blogId);
        return commentService.getCommentsByBlogId(blogId);
    }

    /**
     * AJAX检查用户是否可以删除评论
     */
    @GetMapping("/comment/{commentId}/can-delete")
    @ResponseBody
    public boolean canDeleteComment(@PathVariable Long commentId) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return commentService.canDeleteComment(commentId, currentUser.getId());
    }
}