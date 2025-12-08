package com.github.toran.module.content.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.toran.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类实体
 *
 * @author toran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_category")
public class BlogCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
}
