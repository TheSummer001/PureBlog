package com.github.toran.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博客公开配置 VO
 * 注意：仅包含前端需要的公开配置，禁止包含后端敏感信息
 *
 * @author toran
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "博客公开配置")
public class BlogConfigVO {

    /**
     * 站点信息
     */
    @Schema(description = "站点信息")
    private SiteInfo site;

    /**
     * Giscus 评论配置
     */
    @Schema(description = "Giscus 评论配置")
    private GiscusConfig giscus;

    /**
     * 站点信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SiteInfo {
        @Schema(description = "站点名称")
        private String name;

        @Schema(description = "站点描述")
        private String description;

        @Schema(description = "ICP 备案号")
        private String icp;
    }

    /**
     * Giscus 配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiscusConfig {
        @Schema(description = "GitHub 仓库（格式：owner/repo）")
        private String repo;

        @Schema(description = "仓库 ID")
        private String repoId;

        @Schema(description = "分类名称")
        private String category;

        @Schema(description = "分类 ID")
        private String categoryId;

        @Schema(description = "映射方式（pathname/url/title）")
        private String mapping;
    }
}
