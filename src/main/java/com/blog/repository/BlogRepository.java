package com.blog.repository;

import com.blog.entity.Blog;
import com.blog.entity.Tag;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 博客数据访问接口
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * 查找已发布的博客，按创建时间倒序分页
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    Page<Blog> findByPublishedOrderByCreatedAtDesc(Boolean published, Pageable pageable);

    /**
     * 根据作者查找博客，按创建时间倒序分页
     * @param author 作者
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    Page<Blog> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 根据作者ID查找博客，按创建时间倒序分页
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    Page<Blog> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * 根据作者和发布状态查找博客
     * @param author 作者
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    Page<Blog> findByAuthorAndPublishedOrderByCreatedAtDesc(User author, Boolean published, Pageable pageable);

    /**
     * 在标题和内容中搜索关键词（已发布的博客）
     * @param keyword 搜索关键词
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    @Query("SELECT b FROM Blog b WHERE b.published = :published AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY b.createdAt DESC")
    Page<Blog> searchByKeyword(@Param("keyword") String keyword, 
                              @Param("published") Boolean published, 
                              Pageable pageable);

    /**
     * 根据标签查找博客（已发布的博客）
     * @param tag 标签
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    @Query("SELECT b FROM Blog b JOIN b.tags t WHERE t = :tag AND b.published = :published ORDER BY b.createdAt DESC")
    Page<Blog> findByTagAndPublished(@Param("tag") Tag tag, 
                                    @Param("published") Boolean published, 
                                    Pageable pageable);

    /**
     * 根据标签名称查找博客（已发布的博客）
     * @param tagName 标签名称
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    @Query("SELECT b FROM Blog b JOIN b.tags t WHERE t.name = :tagName AND b.published = :published ORDER BY b.createdAt DESC")
    Page<Blog> findByTagNameAndPublished(@Param("tagName") String tagName, 
                                        @Param("published") Boolean published, 
                                        Pageable pageable);

    /**
     * 获取最受欢迎的博客（按浏览量排序）
     * @param published 发布状态
     * @param pageable 分页参数
     * @return 博客分页结果
     */
    Page<Blog> findByPublishedOrderByViewCountDesc(Boolean published, Pageable pageable);

    /**
     * 获取最新的博客
     * @param published 发布状态
     * @param limit 限制数量
     * @return 博客列表
     */
    @Query("SELECT b FROM Blog b WHERE b.published = :published ORDER BY b.createdAt DESC")
    List<Blog> findLatestBlogs(@Param("published") Boolean published, Pageable pageable);

    /**
     * 增加博客浏览量
     * @param blogId 博客ID
     */
    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :blogId")
    void incrementViewCount(@Param("blogId") Long blogId);

    /**
     * 统计博客总数
     * @return 博客总数
     */
    @Query("SELECT COUNT(b) FROM Blog b")
    long countBlogs();

    /**
     * 统计已发布的博客数量
     * @return 已发布的博客数量
     */
    @Query("SELECT COUNT(b) FROM Blog b WHERE b.published = true")
    long countPublishedBlogs();

    /**
     * 统计指定作者的博客数量
     * @param author 作者
     * @return 博客数量
     */
    long countByAuthor(User author);

    /**
     * 统计指定作者已发布的博客数量
     * @param author 作者
     * @param published 发布状态
     * @return 博客数量
     */
    long countByAuthorAndPublished(User author, Boolean published);

    /**
     * 查找指定博客ID和作者的博客（用于权限验证）
     * @param id 博客ID
     * @param author 作者
     * @return 博客对象
     */
    Optional<Blog> findByIdAndAuthor(Long id, User author);

    /**
     * 获取博客的总浏览量
     * @return 总浏览量
     */
    @Query("SELECT SUM(b.viewCount) FROM Blog b WHERE b.published = true")
    Long getTotalViewCount();

    /**
     * 获取指定作者的博客总浏览量
     * @param author 作者
     * @return 总浏览量
     */
    @Query("SELECT SUM(b.viewCount) FROM Blog b WHERE b.author = :author AND b.published = true")
    Long getTotalViewCountByAuthor(@Param("author") User author);
}