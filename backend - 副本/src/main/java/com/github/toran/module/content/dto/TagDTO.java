package com.github.toran.module.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标签提交 DTO
 *
 * @author toran
 */
@Data
public class TagDTO {

    /**
     * 标签 ID（修改时必填）
     */
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签颜色（如：#1890ff）
     */
    private String color;

    /**
     * 状态（0-禁用，1-启用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
