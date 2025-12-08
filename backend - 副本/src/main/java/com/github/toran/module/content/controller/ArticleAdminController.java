package com.github.toran.module.content.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.toran.common.core.domain.Result;
import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.module.content.dto.ArticleQueryDTO;
import com.github.toran.module.content.dto.ArticleSubmitDTO;
import com.github.toran.module.content.service.IArticleService;
import com.github.toran.module.content.vo.ArticleDetailVO;
import com.github.toran.module.content.vo.ArticleListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 文章管理 Controller（管理端）
 *
 * @author toran
 */
@Tag(name = "文章管理（管理端）", description = "文章的增删改查接口，需要管理员权限")
@RestController
@RequestMapping("/api/admin/article")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final IArticleService articleService;

    @Operation(summary = "发布文章", description = "新增文章")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Long> createArticle(@Valid @RequestBody ArticleSubmitDTO dto) {
        Long authorId = getCurrentUserId();
        Long articleId = articleService.saveOrUpdateArticle(dto, authorId);
        return Result.success("文章发布成功", articleId);
    }

    @Operation(summary = "修改文章", description = "更新文章")
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Long> updateArticle(@Valid @RequestBody ArticleSubmitDTO dto) {
        if (dto.getId() == null) {
            return Result.error("文章ID不能为空");
        }
        Long authorId = getCurrentUserId();
        Long articleId = articleService.saveOrUpdateArticle(dto, authorId);
        return Result.success("文章修改成功", articleId);
    }

    @Operation(summary = "删除文章", description = "逻辑删除文章")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("文章删除成功");
    }

    @Operation(summary = "文章列表", description = "分页查询文章列表（支持按标题、分类、状态筛选）")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Page<ArticleListVO>> getArticleList(ArticleQueryDTO query) {
        Page<ArticleListVO> page = articleService.getArticleListForAdmin(query);
        return Result.success(page);
    }

    @Operation(summary = "文章详情", description = "获取文章详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {
        ArticleDetailVO detail = articleService.getArticleDetail(id, false);
        return Result.success(detail);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            return loginUser.getUserId();
        }
        throw new RuntimeException("未登录或登录已过期");
    }
}
