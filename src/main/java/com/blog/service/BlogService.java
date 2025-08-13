package com.blog.service;

import com.blog.dto.BlogDto;
import com.blog.entity.Blog;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 博客服务类
 */
@Service
@Transactional
public class BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * 创建博客
     */
    public Blog createBlog(BlogDto blogDto, Long authorId) {
        logger.info("开始创建博客: {}, 作者ID: {}", blogDto.getTitle(), authorId);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + authorId));

        // 创建博客实体
        Blog blog = new Blog();
        blog.setTitle(blogDto.getTitle());
        blog.setContent(blogDto.getContent());
        blog.setSummary(blogDto.getSummary());
        blog.setPublished(blogDto.getPublished() != null ? blogDto.getPublished() : true);
        blog.setAuthor(author);

        // 处理标签
        if (blogDto.getTags() != null && !blogDto.getTags().trim().isEmpty()) {
            Set<Tag> tags = processTags(blogDto.getTags());
            blog.setTags(tags);
        }

        Blog savedBlog = blogRepository.save(blog);
        logger.info("博客创建成功: {} (ID: {})", savedBlog.getTitle(), savedBlog.getId());

        return savedBlog;
    }

    /**
     * 根据ID获取博客
     */
    @Transactional(readOnly = true)
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    /**
     * 根据ID获取博客并增加浏览量
     */
    public Optional<Blog> getBlogByIdAndIncrementView(Long id) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isPresent()) {
            Blog blog = blogOpt.get();
            blog.incrementViewCount();
            blogRepository.save(blog);
        }
        return blogOpt;
    }

    /**
     * 获取所有已发布的博客（分页）
     */
    @Transactional(readOnly = true)
    public Page<Blog> getAllPublishedBlogs(Pageable pageable) {
        return blogRepository.findByPublishedOrderByCreatedAtDesc(true, pageable);
    }

    /**
     * 根据作者获取博客（分页）
     */
    @Transactional(readOnly = true)
    public Page<Blog> getBlogsByAuthor(Long authorId, Pageable pageable) {
        return blogRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }

    /**
     * 根据作者和发布状态获取博客（分页）
     */
    @Transactional(readOnly = true)
    public Page<Blog> getBlogsByAuthorAndPublished(Long authorId, Boolean published, Pageable pageable) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + authorId));
        return blogRepository.findByAuthorAndPublishedOrderByCreatedAtDesc(author, published, pageable);
    }

    /**
     * 更新博客
     */
    public Blog updateBlog(Long blogId, BlogDto blogDto, Long authorId) {
        logger.info("开始更新博客: {} (ID: {}), 作者ID: {}", blogDto.getTitle(), blogId, authorId);

        Blog blog = blogRepository.findByIdAndAuthor(blogId, 
                userRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + authorId)))
                .orElseThrow(() -> new IllegalArgumentException("博客不存在或您没有权限编辑，ID: " + blogId));

        // 更新博客信息
        blog.setTitle(blogDto.getTitle());
        blog.setContent(blogDto.getContent());
        blog.setSummary(blogDto.getSummary());
        blog.setPublished(blogDto.getPublished() != null ? blogDto.getPublished() : true);

        // 处理标签
        blog.getTags().clear(); // 清除现有标签
        if (blogDto.getTags() != null && !blogDto.getTags().trim().isEmpty()) {
            Set<Tag> tags = processTags(blogDto.getTags());
            blog.setTags(tags);
        }

        Blog updatedBlog = blogRepository.save(blog);
        logger.info("博客更新成功: {} (ID: {})", updatedBlog.getTitle(), updatedBlog.getId());

        return updatedBlog;
    }

    /**
     * 删除博客
     */
    public void deleteBlog(Long blogId, Long authorId) {
        logger.info("开始删除博客，ID: {}, 作者ID: {}", blogId, authorId);

        Blog blog = blogRepository.findByIdAndAuthor(blogId, 
                userRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + authorId)))
                .orElseThrow(() -> new IllegalArgumentException("博客不存在或您没有权限删除，ID: " + blogId));

        blogRepository.delete(blog);
        logger.info("博客删除成功，ID: {}", blogId);
    }

    /**
     * 搜索博客
     */
    @Transactional(readOnly = true)
    public Page<Blog> searchBlogs(String keyword, Pageable pageable) {
        return blogRepository.searchByKeyword(keyword, true, pageable);
    }

    /**
     * 根据标签查找博客
     */
    @Transactional(readOnly = true)
    public Page<Blog> getBlogsByTag(String tagName, Pageable pageable) {
        return blogRepository.findByTagNameAndPublished(tagName, true, pageable);
    }

    /**
     * 获取最受欢迎的博客
     */
    @Transactional(readOnly = true)
    public Page<Blog> getPopularBlogs(Pageable pageable) {
        return blogRepository.findByPublishedOrderByViewCountDesc(true, pageable);
    }

    /**
     * 统计博客数量
     */
    @Transactional(readOnly = true)
    public long getBlogCount() {
        return blogRepository.countBlogs();
    }

    /**
     * 统计已发布博客数量
     */
    @Transactional(readOnly = true)
    public long getPublishedBlogCount() {
        return blogRepository.countPublishedBlogs();
    }

    /**
     * 统计指定作者的博客数量
     */
    @Transactional(readOnly = true)
    public long getBlogCountByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + authorId));
        return blogRepository.countByAuthor(author);
    }

    /**
     * 将Blog实体转换为BlogDto
     */
    public BlogDto convertToBlogDto(Blog blog) {
        BlogDto dto = new BlogDto();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setSummary(blog.getSummary());
        dto.setPublished(blog.getPublished());
        dto.setAuthorId(blog.getAuthor().getId());
        dto.setAuthorName(blog.getAuthor().getDisplayName() != null ? 
                         blog.getAuthor().getDisplayName() : blog.getAuthor().getUsername());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setViewCount(blog.getViewCount());
        dto.setCommentCount(blog.getCommentCount());
        
        // 处理标签
        if (blog.getTags() != null && !blog.getTags().isEmpty()) {
            String tagsString = blog.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", "));
            dto.setTags(tagsString);
        }
        
        return dto;
    }

    /**
     * 处理标签字符串，创建或获取标签实体
     */
    private Set<Tag> processTags(String tagsString) {
        Set<Tag> tags = new HashSet<>();
        
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            String[] tagNames = tagsString.split("[,，]"); // 支持中英文逗号分隔
            
            for (String tagName : tagNames) {
                String trimmedName = tagName.trim();
                if (!trimmedName.isEmpty()) {
                    Tag tag = tagRepository.findByName(trimmedName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag(trimmedName);
                                return tagRepository.save(newTag);
                            });
                    tags.add(tag);
                }
            }
        }
        
        return tags;
    }

    /**
     * 检查用户是否有权限编辑博客
     */
    @Transactional(readOnly = true)
    public boolean canEditBlog(Long blogId, Long userId) {
        return blogRepository.findByIdAndAuthor(blogId, 
                userRepository.findById(userId).orElse(null)).isPresent();
    }
}