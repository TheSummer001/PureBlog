package com.github.toran.module.system.controller;

import com.github.toran.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查 Controller
 *
 * @author toran
 */
@Tag(name = "系统管理", description = "系统相关接口")
@RestController
@RequestMapping("/api/public/system")
public class SystemController {

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0");
        data.put("description", "Personal Blog System is running");

        return Result.success(data);
    }

    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Personal Blog System");
        data.put("version", "1.0.0");
        data.put("author", "toran");
        data.put("framework", "Spring Boot 3.5.8");
        data.put("database", "MySQL 8.0");
        data.put("architecture", "DDD + Clean Architecture");

        return Result.success(data);
    }
}
