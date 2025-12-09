package com.github.toran.module.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.vo.ArticleDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {

    /**
     * 增加文章阅读量
     *
     * @param articleId 文章ID
     */
    @Update("UPDATE blog_article SET views = views + 1 WHERE id = #{articleId}")
    void incrementViews(@Param("articleId") Long articleId);

    /**
     * 统计总阅读量
     *
     * @return 总阅读量
     */
    @Select("SELECT COALESCE(SUM(views), 0) FROM blog_article WHERE deleted = 0")
    Long sumViews();

    /**
     * 按日期统计文章发布数量（优化性能，避免查询大字段）
     * 解决 OOM 问题：只查询日期和数量，不查询 content 等大字段
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return Map<日期字符串, 数量>
     */
    @Select("SELECT DATE(publish_time) as date, COUNT(*) as count " +
            "FROM blog_article " +
            "WHERE deleted = 0 " +
            "AND status = 1 " +
            "AND publish_time >= #{startTime} " +
            "AND publish_time <= #{endTime} " +
            "GROUP BY DATE(publish_time)")
    List<Map<String, Object>> selectPublishCount(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select("SELECT t1.id, t1.title, t1.summary, t1.cover, t1.create_time, t2.nickname as authorName, t2.avatar as authorAvatar " +
            "FROM blog_article t1 " +
            "LEFT JOIN sys_user t2 ON t1.user_id = t2.id " +
            "WHERE t1.del_flag = 0 " +
            "ORDER BY t1.create_time DESC " +
            "LIMIT #{limit}")
    List<ArticleDetailVO> selectRecentArticlesForDashboard(int limit);
}
