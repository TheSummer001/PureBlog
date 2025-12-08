package com.github.toran.module.content.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.dto.ArticleQueryDTO;
import com.github.toran.module.content.service.IArticleService;
import com.github.toran.module.content.vo.ArticleDetailVO;
import com.github.toran.module.content.vo.ArticleListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章 Controller（前台公开接口）
 *
 * @author toran
 */
@Tag(name = "文章管理（前台）", description = "前台文章查询接口，无需登录")
@RestController
@RequestMapping("/api/public/article")
@RequiredArgsConstructor
public class ArticlePublicController {

    private final IArticleService articleService;

    @Operation(summary = "文章列表", description = "分页查询已发布文章列表（支持按分类、标签筛选）")
    @GetMapping("/list")
    public Result<Page<ArticleListVO>> getArticleList(ArticleQueryDTO query) {
        // 前台只查询已发布的文章
        query.setStatus(1);
        Page<ArticleListVO> page = articleService.getArticleListForPublic(query);
        return Result.success(page);
    }

    @Operation(summary = "文章详情", description = "获取文章详细信息（会增加阅读量）")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {
        // isPublic=true 表示前台访问，会增加阅读量
        ArticleDetailVO detail = articleService.getArticleDetail(id, true);
        return Result.success(detail);
    }

    @Operation(summary = "热门文章", description = "获取阅读量最高的文章列表")
    @GetMapping("/hot")
    public Result<Page<ArticleListVO>> getHotArticles(@RequestParam(defaultValue = "10") Integer limit) {
        ArticleQueryDTO query = new ArticleQueryDTO();
        query.setStatus(1);
        query.setPageNum(1);
        query.setPageSize(limit);
        // TODO: 后续可以优化排序逻辑（按阅读量倒序）
        Page<ArticleListVO> page = articleService.getArticleListForPublic(query);
        return Result.success(page);
    }
}
