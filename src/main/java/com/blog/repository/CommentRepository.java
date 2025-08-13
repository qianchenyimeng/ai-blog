package com.blog.repository;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评论数据访问接口
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据博客查找评论，按创建时间正序排列
     * @param blog 博客
     * @return 评论列表
     */
    List<Comment> findByBlogOrderByCreatedAtAsc(Blog blog);

    /**
     * 根据博客ID查找评论，按创建时间正序排列
     * @param blogId 博客ID
     * @return 评论列表
     */
    List<Comment> findByBlogIdOrderByCreatedAtAsc(Long blogId);

    /**
     * 根据博客查找评论，分页显示
     * @param blog 博客
     * @param pageable 分页参数
     * @return 评论分页结果
     */
    Page<Comment> findByBlogOrderByCreatedAtAsc(Blog blog, Pageable pageable);

    /**
     * 根据用户查找评论，按创建时间倒序排列
     * @param user 用户
     * @param pageable 分页参数
     * @return 评论分页结果
     */
    Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 根据用户ID查找评论，按创建时间倒序排列
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 评论分页结果
     */
    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 统计指定博客的评论数量
     * @param blog 博客
     * @return 评论数量
     */
    long countByBlog(Blog blog);

    /**
     * 统计指定博客ID的评论数量
     * @param blogId 博客ID
     * @return 评论数量
     */
    long countByBlogId(Long blogId);

    /**
     * 统计指定用户的评论数量
     * @param user 用户
     * @return 评论数量
     */
    long countByUser(User user);

    /**
     * 统计指定用户ID的评论数量
     * @param userId 用户ID
     * @return 评论数量
     */
    long countByUserId(Long userId);

    /**
     * 查找指定博客作者的所有评论（用于博客作者管理评论）
     * @param authorId 博客作者ID
     * @param pageable 分页参数
     * @return 评论分页结果
     */
    @Query("SELECT c FROM Comment c WHERE c.blog.author.id = :authorId ORDER BY c.createdAt DESC")
    Page<Comment> findCommentsByBlogAuthor(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * 查找指定博客作者指定博客的所有评论
     * @param blogId 博客ID
     * @param authorId 博客作者ID
     * @return 评论列表
     */
    @Query("SELECT c FROM Comment c WHERE c.blog.id = :blogId AND c.blog.author.id = :authorId ORDER BY c.createdAt ASC")
    List<Comment> findCommentsByBlogAndAuthor(@Param("blogId") Long blogId, @Param("authorId") Long authorId);

    /**
     * 查找指定评论ID和博客作者的评论（用于权限验证）
     * @param commentId 评论ID
     * @param authorId 博客作者ID
     * @return 评论对象
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.blog.author.id = :authorId")
    Optional<Comment> findByIdAndBlogAuthor(@Param("commentId") Long commentId, @Param("authorId") Long authorId);

    /**
     * 获取最新的评论
     * @param limit 限制数量
     * @return 评论列表
     */
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findLatestComments(Pageable pageable);

    /**
     * 统计评论总数
     * @return 评论总数
     */
    @Query("SELECT COUNT(c) FROM Comment c")
    long countComments();

    /**
     * 根据内容搜索评论
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 评论分页结果
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.createdAt DESC")
    Page<Comment> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 删除指定博客的所有评论
     * @param blog 博客
     */
    void deleteByBlog(Blog blog);

    /**
     * 删除指定用户的所有评论
     * @param user 用户
     */
    void deleteByUser(User user);
}