package com.github.toran.module.content.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.toran.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章主表实体
 *
 * @author toran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_article")
public class BlogArticle extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 作者 ID
     */
    private Long authorId;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 阅读量
     */
    private Integer views;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}
