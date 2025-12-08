package com.github.toran.framework.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.mapper.BlogArticleMapper;
import com.github.toran.module.content.service.IArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 文章阅读量同步定时任务
 * 每 10 分钟将 Redis 中的阅读量同步到 MySQL
 * 
 * 设计考虑：
 * 1. 使用 Redis incr 原子操作，避免高并发下数据库压力
 * 2. 定时批量同步，减少数据库写操作
 * 3. 同步后不删除 Redis 数据，保持实时性
 *
 * @author toran
 */
@Slf4j
@Component
public class ViewCountSyncTask {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BlogArticleMapper articleMapper;
    private final IArticleService articleService;

    public ViewCountSyncTask(RedisTemplate<String, Object> redisTemplate,
            BlogArticleMapper articleMapper,
            IArticleService articleService) {
        this.redisTemplate = redisTemplate;
        this.articleMapper = articleMapper;
        this.articleService = articleService;
    }

    /**
     * Redis 中文章阅读量的 Key 前缀
     */
    private static final String ARTICLE_VIEWS_CACHE_PREFIX = "blog:views:";

    /**
     * 定时同步文章阅读量到数据库
     * 每 10 分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void syncArticleViewsToDatabase() {
        log.info("开始同步文章阅读量到数据库...");

        try {
            // 1. 获取所有文章阅读量的 Redis Key
            Set<String> keys = redisTemplate.keys(ARTICLE_VIEWS_CACHE_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                log.info("没有需要同步的阅读量数据");
                return;
            }

            int successCount = 0;
            int failureCount = 0;

            // 2. 遍历所有 Key，逐个同步
            for (String key : keys) {
                try {
                    // 解析文章 ID
                    String articleIdStr = key.replace(ARTICLE_VIEWS_CACHE_PREFIX, "");
                    Long articleId = Long.parseLong(articleIdStr);

                    // 从 Redis 获取当前阅读量
                    Object viewsObj = redisTemplate.opsForValue().get(key);
                    if (viewsObj == null) {
                        continue;
                    }

                    Integer redisViews = Integer.parseInt(viewsObj.toString());

                    // 优化 N+1 问题：使用 update(Wrapper) 直接更新，避免 selectById 查询
                    LambdaUpdateWrapper<BlogArticle> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(BlogArticle::getId, articleId)
                            .lt(BlogArticle::getViews, redisViews) // 只有当 Redis 中的值更大时才更新
                            .set(BlogArticle::getViews, redisViews);

                    int updateResult = articleMapper.update(null, updateWrapper);
                    if (updateResult > 0) {
                        successCount++;
                        log.debug("同步文章阅读量成功: articleId={}, redisViews={}", articleId, redisViews);
                    } else {
                        // updateResult == 0 表示没有满足条件的记录（可能是数据库值更大或记录不存在）
                        log.debug("无需同步文章阅读量: articleId={}, redisViews={}", articleId, redisViews);
                    }

                } catch (Exception e) {
                    failureCount++;
                    log.error("同步单个文章阅读量失败: key={}", key, e);
                }
            }

            log.info("文章阅读量同步完成: 总数={}, 成功={}, 失败={}",
                    keys.size(), successCount, failureCount);

        } catch (Exception e) {
            log.error("同步文章阅读量到数据库失败", e);
        }
    }

    /**
     * 系统启动时执行一次同步（延迟 30 秒启动）
     * 避免 Redis 连接未就绪
     */
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE)
    public void syncOnStartup() {
        log.info("系统启动后首次同步文章阅读量...");
        syncArticleViewsToDatabase();
    }
}