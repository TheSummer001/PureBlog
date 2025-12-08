package com.github.toran.module.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类提交 DTO
 *
 * @author toran
 */
@Data
public class CategoryDTO {

    /**
     * 分类 ID（修改时必填）
     */
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序（数字越小越靠前）
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
