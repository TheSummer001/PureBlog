package com.github.toran.module.content.dto;

import lombok.Data;

/**
 * 文章查询条件 DTO
 *
 * @author toran
 */
@Data
public class ArticleQueryDTO {

    /**
     * 文章标题（模糊查询）
     */
    private String title;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 标签 ID
     */
    private Long tagId;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
