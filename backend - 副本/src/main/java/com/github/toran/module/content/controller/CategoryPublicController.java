package com.github.toran.module.content.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.service.ICategoryService;
import com.github.toran.module.content.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类管理 Controller（前台）
 *
 * @author toran
 */
@Tag(name = "分类管理（前台）", description = "前台分类查询接口，无需登录")
@RestController
@RequestMapping("/api/public/category")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final ICategoryService categoryService;

    @Operation(summary = "分类列表", description = "获取所有启用的分类（用于前端菜单/下拉框）")
    @GetMapping("/list")
    public Result<List<CategoryVO>> getCategoryList() {
        List<CategoryVO> list = categoryService.listAllEnabled();
        return Result.success(list);
    }
}
