package com.github.toran.module.content.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.service.ITagService;
import com.github.toran.module.content.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签管理 Controller（前台）
 *
 * @author toran
 */
@Tag(name = "标签管理（前台）", description = "前台标签查询接口，无需登录")
@RestController
@RequestMapping("/api/public/tag")
@RequiredArgsConstructor
public class TagPublicController {

    private final ITagService tagService;

    @Operation(summary = "标签列表", description = "获取所有启用的标签（用于标签云/下拉框）")
    @GetMapping("/list")
    public Result<List<TagVO>> getTagList() {
        List<TagVO> list = tagService.listAllEnabled();
        return Result.success(list);
    }
}
