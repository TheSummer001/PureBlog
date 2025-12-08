package com.github.toran.module.content.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.dto.TagDTO;
import com.github.toran.module.content.service.ITagService;
import com.github.toran.module.content.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理 Controller（管理端）
 *
 * @author toran
 */
@Tag(name = "标签管理（管理端）", description = "标签的增删改查接口，需要管理员权限")
@RestController
@RequestMapping("/api/admin/tag")
@RequiredArgsConstructor
public class TagAdminController {

    private final ITagService tagService;

    @Operation(summary = "创建标签", description = "新增文章标签")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Long> createTag(@Valid @RequestBody TagDTO dto) {
        Long id = tagService.createTag(dto.getName(), dto.getColor(), dto.getStatus());
        return Result.success("标签创建成功", id);
    }

    @Operation(summary = "更新标签", description = "修改标签信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateTag(@PathVariable Long id, @Valid @RequestBody TagDTO dto) {
        tagService.updateTag(id, dto.getName(), dto.getColor(), dto.getStatus());
        return Result.success("标签更新成功");
    }

    @Operation(summary = "删除标签", description = "删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("标签删除成功");
    }

    @Operation(summary = "标签列表", description = "获取所有标签")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<TagVO>> getTagList() {
        List<TagVO> list = tagService.listAllEnabled();
        return Result.success(list);
    }

    @Operation(summary = "标签详情", description = "获取标签详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<TagVO> getTagDetail(@PathVariable Long id) {
        TagVO detail = tagService.getTagDetail(id);
        return Result.success(detail);
    }
}
