package com.github.toran.module.content.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.dto.CategoryDTO;
import com.github.toran.module.content.service.ICategoryService;
import com.github.toran.module.content.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理 Controller（管理端）
 *
 * @author toran
 */
@Tag(name = "分类管理（管理端）", description = "分类的增删改查接口，需要管理员权限")
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final ICategoryService categoryService;

    @Operation(summary = "创建分类", description = "新增文章分类")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Long> createCategory(@Valid @RequestBody CategoryDTO dto) {
        Long id = categoryService.createCategory(dto.getName(), dto.getDescription(), dto.getSort(), dto.getStatus());
        return Result.success("分类创建成功", id);
    }

    @Operation(summary = "更新分类", description = "修改分类信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(id, dto.getName(), dto.getDescription(), dto.getSort(), dto.getStatus());
        return Result.success("分类更新成功");
    }

    @Operation(summary = "删除分类", description = "删除分类（需检查是否有文章关联）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功");
    }

    @Operation(summary = "分类列表", description = "获取所有分类（包含文章数量）")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<CategoryVO>> getCategoryList() {
        List<CategoryVO> list = categoryService.listAllEnabled();
        return Result.success(list);
    }

    @Operation(summary = "分类详情", description = "获取分类详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<CategoryVO> getCategoryDetail(@PathVariable Long id) {
        CategoryVO detail = categoryService.getCategoryDetail(id);
        return Result.success(detail);
    }
}
