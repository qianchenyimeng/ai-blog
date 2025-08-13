package com.blog.service;

import com.blog.dto.CommentDto;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评论服务类
 */
@Service
@Transactional
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 添加评论
     */
    public Comment addComment(Long blogId, CommentDto commentDto, Long userId) {
        logger.info("开始添加评论，博客ID: {}, 用户ID: {}", blogId, userId);

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("博客不存在，ID: " + blogId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + userId));

        // 创建评论实体
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setBlog(blog);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        logger.info("评论添加成功，ID: {}", savedComment.getId());

        return savedComment;
    }

    /**
     * 根据博客ID获取评论列表
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBlogId(Long blogId) {
        List<Comment> comments = commentRepository.findByBlogIdOrderByCreatedAtAsc(blogId);
        return comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据博客获取评论列表（分页）
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByBlog(Long blogId, Pageable pageable) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("博客不存在，ID: " + blogId));
        return commentRepository.findByBlogOrderByCreatedAtAsc(blog, pageable);
    }

    /**
     * 根据用户获取评论列表（分页）
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 删除评论（仅博客作者可删除）
     */
    public void deleteComment(Long commentId, Long userId) {
        logger.info("开始删除评论，评论ID: {}, 用户ID: {}", commentId, userId);

        Comment comment = commentRepository.findByIdAndBlogAuthor(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在或您没有权限删除，ID: " + commentId));

        commentRepository.delete(comment);
        logger.info("评论删除成功，ID: {}", commentId);
    }

    /**
     * 根据博客作者获取评论列表（用于博客作者管理评论）
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByBlogAuthor(Long authorId, Pageable pageable) {
        return commentRepository.findCommentsByBlogAuthor(authorId, pageable);
    }

    /**
     * 根据博客ID和作者ID获取评论列表
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBlogAndAuthor(Long blogId, Long authorId) {
        List<Comment> comments = commentRepository.findCommentsByBlogAndAuthor(blogId, authorId);
        return comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * 统计指定博客的评论数量
     */
    @Transactional(readOnly = true)
    public long getCommentCountByBlog(Long blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    /**
     * 统计指定用户的评论数量
     */
    @Transactional(readOnly = true)
    public long getCommentCountByUser(Long userId) {
        return commentRepository.countByUserId(userId);
    }

    /**
     * 获取最新评论
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getLatestComments(int limit) {
        List<Comment> comments = commentRepository.findLatestComments(
                org.springframework.data.domain.PageRequest.of(0, limit));
        return comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * 搜索评论
     */
    @Transactional(readOnly = true)
    public Page<Comment> searchComments(String keyword, Pageable pageable) {
        return commentRepository.searchByContent(keyword, pageable);
    }

    /**
     * 检查用户是否可以删除评论（博客作者可以删除）
     */
    @Transactional(readOnly = true)
    public boolean canDeleteComment(Long commentId, Long userId) {
        return commentRepository.findByIdAndBlogAuthor(commentId, userId).isPresent();
    }

    /**
     * 根据评论ID获取评论
     */
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    /**
     * 将Comment实体转换为CommentDto
     */
    public CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setBlogId(comment.getBlog().getId());
        dto.setBlogTitle(comment.getBlog().getTitle());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getUsername());
        dto.setUserDisplayName(comment.getUser().getDisplayName());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    /**
     * 统计评论总数
     */
    @Transactional(readOnly = true)
    public long getTotalCommentCount() {
        return commentRepository.countComments();
    }
}