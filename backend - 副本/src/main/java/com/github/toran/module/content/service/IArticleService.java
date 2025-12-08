package com.github.toran.module.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.toran.module.content.dto.ArticleQueryDTO;
import com.github.toran.module.content.dto.ArticleSubmitDTO;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.vo.ArticleDetailVO;
import com.github.toran.module.content.vo.ArticleListVO;

/**
 * 文章服务接口
 *
 * @author toran
 */
public interface IArticleService extends IService<BlogArticle> {

    /**
     * 发布/修改文章
     *
     * @param dto      文章提交数据
     * @param authorId 作者ID
     * @return 文章ID
     */
    Long saveOrUpdateArticle(ArticleSubmitDTO dto, Long authorId);

    /**
     * 管理端：分页查询文章列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<ArticleListVO> getArticleListForAdmin(ArticleQueryDTO query);

    /**
     * 前台：分页查询已发布文章列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<ArticleListVO> getArticleListForPublic(ArticleQueryDTO query);

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @param isPublic  是否为前台访问（前台访问会增加阅读量）
     * @return 文章详情
     */
    ArticleDetailVO getArticleDetail(Long articleId, boolean isPublic);

    /**
     * 删除文章（逻辑删除）
     *
     * @param articleId 文章ID
     */
    void deleteArticle(Long articleId);
}
