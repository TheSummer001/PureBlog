package com.github.toran.module.content.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情 VO（包含 content 全文）
 *
 * @author toran
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVO {

    /**
     * 文章 ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 封面图片 URL
     */
    private String coverImg;

    /**
     * 分类信息
     */
    private CategoryInfo category;

    /**
     * 作者信息
     */
    private AuthorInfo author;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 阅读量
     */
    private Integer views;

    /**
     * 标签列表
     */
    private List<TagInfo> tags;

    /**
     * 上一篇文章
     */
    private ArticleNavigation prevArticle;

    /**
     * 下一篇文章
     */
    private ArticleNavigation nextArticle;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 分类信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    /**
     * 作者信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }

    /**
     * 标签信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        private Long id;
        private String name;
        private String color;
    }

    /**
     * 文章导航（上一篇/下一篇）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleNavigation {
        private Long id;
        private String title;
    }
}
