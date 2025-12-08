package com.github.toran.common.constant;

/**
 * 通用常量
 *
 * @author toran
 */
public interface CommonConstant {

    /**
     * 逻辑删除状态：未删除
     */
    int NOT_DELETED = 0;

    /**
     * 逻辑删除状态：已删除
     */
    int DELETED = 1;

    /**
     * 启用状态：启用
     */
    int ENABLED = 1;

    /**
     * 启用状态：禁用
     */
    int DISABLED = 0;

    /**
     * 超级管理员角色 ID
     */
    Long SUPER_ADMIN_ROLE_ID = 1L;

    /**
     * JWT Token 前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT Token Header 名称
     */
    String TOKEN_HEADER = "Authorization";

    /**
     * 成功标记
     */
    Integer SUCCESS = 1;

    /**
     * 失败标记
     */
    Integer FAIL = 0;
}
