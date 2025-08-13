package com.blog.controller;

import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 标签控制器
 */
@Controller
@RequestMapping("/tags")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    /**
     * 标签云页面
     */
    @GetMapping
    public String showTagCloud(Model model) {
        logger.debug("显示标签云页面");

        List<Tag> allTags = tagService.getAllTags();
        List<Tag> popularTags = tagService.getMostUsedTags(20);

        model.addAttribute("allTags", allTags);
        model.addAttribute("popularTags", popularTags);
        model.addAttribute("totalTags", tagService.getTagCount());
        model.addAttribute("usedTags", tagService.getUsedTagCount());

        return "blog/tag-cloud";
    }

    /**
     * 根据标签名称跳转到搜索页面
     */
    @GetMapping("/{tagName}")
    public String showBlogsByTag(@PathVariable String tagName) {
        logger.debug("按标签查看博客: {}", tagName);
        return "redirect:/search?tag=" + tagName;
    }

    /**
     * AJAX获取标签建议
     */
    @GetMapping("/suggest")
    @ResponseBody
    public List<Tag> suggestTags(String q) {
        if (q == null || q.trim().isEmpty()) {
            return tagService.getMostUsedTags(10);
        }
        return tagService.searchTagsByName(q.trim());
    }

    /**
     * AJAX获取热门标签
     */
    @GetMapping("/popular")
    @ResponseBody
    public List<Tag> getPopularTags() {
        return tagService.getMostUsedTags(10);
    }
}