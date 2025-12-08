package com.github.toran.module.content.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 仪表盘统计数据 VO
 *
 * @author toran
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {

    /**
     * 核心统计数据
     */
    private Stats stats;

    /**
     * 发布记录（用于热力图）
     */
    private List<PublishRecord> publishRecords;

    /**
     * 核心统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        /**
         * 文章总数
         */
        private Long articleCount;

        /**
         * 总阅读量
         */
        private Long viewCount;

        /**
         * 分类总数
         */
        private Long categoryCount;

        /**
         * 标签总数
         */
        private Long tagCount;
    }

    /**
     * 发布记录（用于类似 GitHub 贡献图的热力图）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublishRecord {
        /**
         * 日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        /**
         * 当天发布文章数量
         */
        private Integer count;
    }
}
