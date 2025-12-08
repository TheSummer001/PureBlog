package com.github.toran.module.content.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.toran.common.core.domain.Result;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.entity.BlogCategory;
import com.github.toran.module.content.entity.BlogTag;
import com.github.toran.module.content.mapper.BlogArticleMapper;
import com.github.toran.module.content.mapper.BlogCategoryMapper;
import com.github.toran.module.content.mapper.BlogTagMapper;
import com.github.toran.module.content.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘统计 Controller（管理端）
 *
 * @author toran
 */
@Slf4j
@Tag(name = "仪表盘统计（管理端）", description = "提供核心统计数据和发布热力图")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BlogArticleMapper articleMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogTagMapper tagMapper;

    @Operation(summary = "核心统计数据", description = "获取文章总数、总阅读量、分类数、标签数")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<DashboardVO.Stats> getStats() {
        // 文章总数
        Long articleCount = articleMapper.selectCount(null);

        // 总阅读量（求和）
        Long viewCount = articleMapper.sumViews();
        if (viewCount == null) {
            viewCount = 0L;
        }

        // 分类总数
        Long categoryCount = categoryMapper.selectCount(null);

        // 标签总数
        Long tagCount = tagMapper.selectCount(null);

        DashboardVO.Stats stats = DashboardVO.Stats.builder()
                .articleCount(articleCount)
                .viewCount(viewCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .build();

        return Result.success(stats);
    }

    @Operation(summary = "文章发布热力图", description = "获取指定天数内的文章发布记录（类似 GitHub 贡献图）")
    @GetMapping("/publish-record")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<DashboardVO.PublishRecord>> getPublishRecord(
            @RequestParam(defaultValue = "365") Integer days) {

        // 计算开始日期和结束日期
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查询指定日期范围内的文章
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        // 修复 OOM 风险：使用数据库 GROUP BY 统计，避免查询 content 大字段
        List<Map<String, Object>> dateCountList = articleMapper.selectPublishCount(startDateTime, endDateTime);

        // 转换为 Map<LocalDate, Long> 便于查找
        Map<LocalDate, Long> dateCountMap = dateCountList.stream()
                .collect(Collectors.toMap(
                        item -> LocalDate.parse((String) item.get("date")),
                        item -> (Long) item.get("count")));

        // 构建完整的日期列表（包括没有发布文章的日期）
        List<DashboardVO.PublishRecord> records = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Long count = dateCountMap.getOrDefault(currentDate, 0L);
            records.add(DashboardVO.PublishRecord.builder()
                    .date(currentDate)
                    .count(count.intValue())
                    .build());
            currentDate = currentDate.plusDays(1);
        }

        log.debug("查询发布记录: startDate={}, endDate={}, totalDays={}",
                startDate, endDate, days);

        return Result.success(records);
    }

    @Operation(summary = "近 7 天发布趋势", description = "快捷获取近 7 天的文章发布记录")
    @GetMapping("/publish-record/week")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<DashboardVO.PublishRecord>> getWeekPublishRecord() {
        return getPublishRecord(7);
    }

    @Operation(summary = "近 30 天发布趋势", description = "快捷获取近 30 天的文章发布记录")
    @GetMapping("/publish-record/month")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<DashboardVO.PublishRecord>> getMonthPublishRecord() {
        return getPublishRecord(30);
    }
}
