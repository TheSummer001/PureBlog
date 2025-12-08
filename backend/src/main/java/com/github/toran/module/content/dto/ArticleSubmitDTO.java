package com.github.toran.module.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 文章提交 DTO（新增/修改）
 *
 * @author toran
 */
@Data
public class ArticleSubmitDTO {

    /**
     * 文章 ID（修改时必传）
     */
    private Long id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    private String title;

    /**
     * 文章摘要（可选，如果为空则自动截取 content 前100字）
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 封面图片 URL
     */
    private String coverImg;

    /**
     * 分类 ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 标签 ID 列表
     */
    private List<Long> tagIds;
}
