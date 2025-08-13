package com.blog.service;

import com.blog.entity.Tag;
import com.blog.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 标签服务类
 */
@Service
@Transactional
public class TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagService.class);

    @Autowired
    private TagRepository tagRepository;

    /**
     * 获取所有标签，按名称排序
     */
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAllOrderByName();
    }

    /**
     * 获取最常用的标签
     */
    @Transactional(readOnly = true)
    public List<Tag> getMostUsedTags(int limit) {
        return tagRepository.findMostUsedTags(limit);
    }

    /**
     * 根据名称查找标签
     */
    @Transactional(readOnly = true)
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * 根据名称模糊搜索标签
     */
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByName(String name) {
        return tagRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * 创建或获取标签
     */
    public Tag createOrGetTag(String name) {
        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }

        return tagRepository.findByName(trimmedName)
                .orElseGet(() -> {
                    Tag newTag = new Tag(trimmedName);
                    Tag savedTag = tagRepository.save(newTag);
                    logger.debug("创建新标签: {}", trimmedName);
                    return savedTag;
                });
    }

    /**
     * 删除未使用的标签
     */
    public void cleanupUnusedTags() {
        List<Tag> unusedTags = tagRepository.findUnusedTags();
        if (!unusedTags.isEmpty()) {
            tagRepository.deleteAll(unusedTags);
            logger.info("清理了 {} 个未使用的标签", unusedTags.size());
        }
    }

    /**
     * 获取标签统计信息
     */
    @Transactional(readOnly = true)
    public long getTagCount() {
        return tagRepository.countTags();
    }

    /**
     * 获取有博客关联的标签数量
     */
    @Transactional(readOnly = true)
    public long getUsedTagCount() {
        return tagRepository.countTagsWithBlogs();
    }

    /**
     * 检查标签是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
}