-- ====================================================
-- Personal Blog System - Content Module Database Schema
-- ====================================================
-- Author: toran
-- Version: 1.0
-- Description: 博客内容管理模块（分类、标签、文章）
-- ====================================================

USE personal_blog;

-- ====================================================
-- 1. 文章分类表 (blog_category)
-- ====================================================
CREATE TABLE `blog_category` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ====================================================
-- 2. 文章标签表 (blog_tag)
-- ====================================================
CREATE TABLE `blog_tag` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `name` VARCHAR(30) NOT NULL COMMENT '标签名称',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '标签颜色（如：#1890ff）',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- ====================================================
-- 3. 文章主表 (blog_article)
-- ====================================================
CREATE TABLE `blog_article` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    `content` LONGTEXT NOT NULL COMMENT '文章内容（Markdown格式）',
    `cover_img` VARCHAR(500) DEFAULT NULL COMMENT '封面图片 URL',
    `category_id` BIGINT NOT NULL COMMENT '分类 ID',
    `author_id` BIGINT NOT NULL COMMENT '作者 ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态（0-草稿，1-已发布）',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
    `views` INT DEFAULT 0 COMMENT '阅读量',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章主表';

-- ====================================================
-- 4. 文章标签关联表 (blog_article_tag)
-- ====================================================
CREATE TABLE `blog_article_tag` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `article_id` BIGINT NOT NULL COMMENT '文章 ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签 ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ====================================================
-- 初始化数据
-- ====================================================

-- 插入示例分类
INSERT INTO `blog_category` (`id`, `name`, `description`, `sort`, `status`) VALUES 
(1, 'Java', 'Java 技术相关文章', 1, 1),
(2, '前端开发', '前端技术相关文章', 2, 1),
(3, '数据库', '数据库技术相关文章', 3, 1),
(4, '架构设计', '系统架构设计相关文章', 4, 1),
(5, '个人随笔', '生活随笔与感悟', 5, 1);

-- 插入示例标签
INSERT INTO `blog_tag` (`id`, `name`, `color`, `status`) VALUES 
(1, 'Spring Boot', '#10b981', 1),
(2, 'MyBatis', '#3b82f6', 1),
(3, 'Vue.js', '#22c55e', 1),
(4, 'React', '#06b6d4', 1),
(5, 'MySQL', '#f59e0b', 1),
(6, 'Redis', '#ef4444', 1),
(7, '微服务', '#8b5cf6', 1),
(8, 'Docker', '#0ea5e9', 1);
