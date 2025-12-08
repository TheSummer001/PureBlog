package com.github.toran.module.content.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.toran.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签实体
 *
 * @author toran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_tag")
public class BlogTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色（如：#1890ff）
     */
    private String color;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
}
