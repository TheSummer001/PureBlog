package com.github.toran.module.system.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.module.system.service.ISysConfigService;
import com.github.toran.module.system.vo.BlogConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开配置 Controller
 * 提供博客公开配置信息（Giscus + 站点信息）
 *
 * @author toran
 */
@Tag(name = "公开配置", description = "博客公开配置接口（无需登录）")
@RestController
@RequestMapping("/api/public/config")
@RequiredArgsConstructor
public class PublicConfigController {

    private final ISysConfigService sysConfigService;

    @Operation(summary = "获取博客配置", description = "获取博客公开配置（Giscus + 站点信息）")
    @GetMapping
    public Result<BlogConfigVO> getBlogConfig() {
        BlogConfigVO config = sysConfigService.getBlogConfig();
        return Result.success(config);
    }
}
