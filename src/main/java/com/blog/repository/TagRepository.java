package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问接口
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据标签名称查找标签
     * @param name 标签名称
     * @return 标签对象
     */
    Optional<Tag> findByName(String name);

    /**
     * 检查标签名称是否存在
     * @param name 标签名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据标签名称列表查找标签
     * @param names 标签名称列表
     * @return 标签列表
     */
    List<Tag> findByNameIn(List<String> names);

    /**
     * 模糊查询标签名称
     * @param name 标签名称关键词
     * @return 标签列表
     */
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:name% ORDER BY t.name")
    List<Tag> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * 获取使用次数最多的标签（根据关联的博客数量）
     * @param limit 限制数量
     * @return 标签列表
     */
    @Query("SELECT t FROM Tag t LEFT JOIN t.blogs b GROUP BY t ORDER BY COUNT(b) DESC")
    List<Tag> findMostUsedTags(@Param("limit") int limit);

    /**
     * 获取所有标签，按名称排序
     * @return 标签列表
     */
    @Query("SELECT t FROM Tag t ORDER BY t.name")
    List<Tag> findAllOrderByName();

    /**
     * 统计标签总数
     * @return 标签总数
     */
    @Query("SELECT COUNT(t) FROM Tag t")
    long countTags();

    /**
     * 统计有博客关联的标签数量
     * @return 有博客关联的标签数量
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Tag t JOIN t.blogs b")
    long countTagsWithBlogs();

    /**
     * 查找没有关联任何博客的标签
     * @return 未使用的标签列表
     */
    @Query("SELECT t FROM Tag t WHERE t.blogs IS EMPTY")
    List<Tag> findUnusedTags();
}