-- ====================================================
-- Personal Blog System - Database Schema
-- ====================================================
-- Author: toran
-- Version: 1.0
-- Description: 数据库表设计（用户、角色、配置等）
-- ====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS personal_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE personal_blog;

-- ====================================================
-- 1. 用户表 (sys_user)
-- ====================================================
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ====================================================
-- 2. 角色表 (sys_role)
-- ====================================================
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL COMMENT '角色标识（用于权限控制）',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ====================================================
-- 3. 用户角色关联表 (sys_user_role)
-- ====================================================
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ====================================================
-- 4. 系统配置表 (sys_config)
-- ====================================================
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
    `is_encrypted` TINYINT DEFAULT 0 COMMENT '是否加密（0-否，1-是）',
    `config_group` VARCHAR(50) DEFAULT NULL COMMENT '配置分组（如：oss、email、giscus）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_group` (`config_group`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ====================================================
-- 初始化数据
-- ====================================================

-- 插入超级管理员（密码：admin，使用 BCrypt 加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`, `remark`) 
VALUES (1, 'admin', '$2a$10$.WL3cftKEtAK.gyf5FJ41eQH5K6gG7QGvgXx6wS/ynKyKZLEvZuaW', '超级管理员', 'admin@example.com', 1, '系统默认超级管理员');

-- 插入角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `remark`) 
VALUES 
(1, '超级管理员', 'ROLE_ADMIN', 1, 1, '拥有系统所有权限'),
(2, '普通用户', 'ROLE_USER', 2, 1, '普通用户权限');

-- 插入用户角色关联
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) 
VALUES (1, 1, 1);

-- 插入系统配置（示例）
INSERT INTO `sys_config` (`id`, `config_key`, `config_value`, `description`, `config_group`) 
VALUES 
(1, 'oss.storage.type', 'local', '文件存储类型（local/oss/minio）', 'oss'),
(2, 'oss.local.path', 'D:/upload/', '本地存储路径', 'oss'),
(3, 'oss.local.url.prefix', 'http://localhost:8080/files/', '本地存储访问 URL 前缀', 'oss'),
(4, 'giscus.repo', '', 'Giscus GitHub 仓库（格式：owner/repo）', 'giscus'),
(5, 'giscus.repo_id', '', 'Giscus 仓库 ID', 'giscus'),
(6, 'giscus.category', 'Announcements', 'Giscus 分类名称', 'giscus'),
(7, 'giscus.category_id', '', 'Giscus 分类 ID', 'giscus'),
(8, 'giscus.mapping', 'pathname', 'Giscus 映射方式（pathname/url/title）', 'giscus'),
(9, 'site.name', '个人博客', '站点名称', 'site'),
(10, 'site.icp', '', 'ICP 备案号', 'site'),
(11, 'site.description', '分享技术与生活', '站点描述', 'site');
