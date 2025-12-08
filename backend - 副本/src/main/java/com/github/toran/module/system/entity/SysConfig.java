package com.github.toran.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.toran.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体
 * 用于动态配置管理（OSS、邮件、Giscus 等）
 *
 * @author toran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 是否加密（0-否，1-是）
     */
    private Integer isEncrypted;

    /**
     * 配置分组（如：oss、email、giscus）
     */
    private String configGroup;

    /**
     * 备注
     */
    private String remark;
}
