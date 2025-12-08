package com.github.toran.module.content.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.common.exception.BizException;
import com.github.toran.module.content.dto.ArticleQueryDTO;
import com.github.toran.module.content.dto.ArticleSubmitDTO;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.entity.BlogArticleTag;
import com.github.toran.module.content.entity.BlogCategory;
import com.github.toran.module.content.entity.BlogTag;
import com.github.toran.module.content.mapper.BlogArticleMapper;
import com.github.toran.module.content.mapper.BlogArticleTagMapper;
import com.github.toran.module.content.mapper.BlogCategoryMapper;
import com.github.toran.module.content.mapper.BlogTagMapper;
import com.github.toran.module.content.service.IArticleService;
import com.github.toran.module.content.vo.ArticleDetailVO;
import com.github.toran.module.content.vo.ArticleListVO;
import com.github.toran.module.system.entity.SysUser;
import com.github.toran.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<BlogArticleMapper, BlogArticle> implements IArticleService {

    private final BlogArticleMapper articleMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogTagMapper tagMapper;
    private final BlogArticleTagMapper articleTagMapper;
    private final SysUserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 文章详情缓存 Key 前缀
     */
    private static final String ARTICLE_DETAIL_CACHE_PREFIX = "blog:article:";

    /**
     * 文章阅读量 Redis Key 前缀
     */
    private static final String ARTICLE_VIEWS_CACHE_PREFIX = "blog:views:";

    /**
     * 缓存过期时间（1 小时）
     */
    private static final long CACHE_EXPIRE_HOURS = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateArticle(ArticleSubmitDTO dto, Long authorId) {
        // 1. 验证分类是否存在
        BlogCategory category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BizException("分类不存在");
        }

        // 2. 构建文章实体
        BlogArticle article = new BlogArticle();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCoverImg(dto.getCoverImg());
        article.setCategoryId(dto.getCategoryId());
        article.setAuthorId(authorId);
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        article.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);

        // 3. 处理摘要：如果没传则自动截取 content 前100字
        if (StrUtil.isBlank(dto.getSummary())) {
            String summary = StrUtil.sub(dto.getContent(), 0, 100);
            article.setSummary(summary);
        } else {
            article.setSummary(dto.getSummary());
        }

        // 4. 如果是发布状态，设置发布时间
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            if (dto.getId() == null) {
                // 新文章发布，设置发布时间
                article.setPublishTime(LocalDateTime.now());
            } else {
                // 修改文章，保持原发布时间
                BlogArticle existArticle = articleMapper.selectById(dto.getId());
                if (existArticle != null && existArticle.getPublishTime() == null) {
                    article.setPublishTime(LocalDateTime.now());
                }
            }
        }

        Long articleId = null;
        // 5. 保存或更新文章
        if (dto.getId() != null) {
            // 更新文章
            article.setId(dto.getId());
            this.updateById(article);
            articleId = article.getId();

            // 修复缓存一致性：更新文章后清除缓存
            clearArticleCache(articleId);
        } else {
            // 新增文章
            article.setViews(0);
            this.save(article);
            articleId = article.getId();
        }

        // 6. 处理标签关联关系
        handleArticleTags(articleId, dto.getTagIds());

        log.info("文章保存成功：articleId={}, title={}", article.getId(), article.getTitle());
        return articleId;
    }

    /**
     * 处理文章标签关联关系
     * 策略：先删除旧关联，再插入新关联
     */
    private void handleArticleTags(Long articleId, List<Long> tagIds) {
        // 删除旧关联
        LambdaQueryWrapper<BlogArticleTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BlogArticleTag::getArticleId, articleId);
        articleTagMapper.delete(deleteWrapper);

        // 插入新关联
        if (CollUtil.isNotEmpty(tagIds)) {
            for (Long tagId : tagIds) {
                BlogArticleTag articleTag = new BlogArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                articleTagMapper.insert(articleTag);
            }
        }
    }

    @Override
    public Page<ArticleListVO> getArticleListForAdmin(ArticleQueryDTO query) {
        // 构建查询条件
        Page<BlogArticle> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<BlogArticle> wrapper = buildQueryWrapper(query, false);

        // 查询分页数据
        Page<BlogArticle> articlePage = this.page(page, wrapper);

        // 转换为 VO
        return convertToArticleListVO(articlePage);
    }

    @Override
    public Page<ArticleListVO> getArticleListForPublic(ArticleQueryDTO query) {
        // 构建查询条件（只查询已发布的文章）
        Page<BlogArticle> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<BlogArticle> wrapper = buildQueryWrapper(query, true);

        // 查询分页数据
        Page<BlogArticle> articlePage = this.page(page, wrapper);

        // 转换为 VO
        return convertToArticleListVO(articlePage);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<BlogArticle> buildQueryWrapper(ArticleQueryDTO query, boolean onlyPublished) {
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        wrapper.like(StrUtil.isNotBlank(query.getTitle()), BlogArticle::getTitle, query.getTitle());

        // 分类筛选
        wrapper.eq(query.getCategoryId() != null, BlogArticle::getCategoryId, query.getCategoryId());

        // 状态筛选
        if (onlyPublished) {
            // 前台只查询已发布的
            wrapper.eq(BlogArticle::getStatus, 1);
        } else {
            // 管理端可以按状态筛选
            wrapper.eq(query.getStatus() != null, BlogArticle::getStatus, query.getStatus());
        }

        // 置顶筛选
        wrapper.eq(query.getIsTop() != null, BlogArticle::getIsTop, query.getIsTop());

        // 标签筛选（需要联表查询）
        if (query.getTagId() != null) {
            wrapper.inSql(BlogArticle::getId,
                    "SELECT article_id FROM blog_article_tag WHERE tag_id = " + query.getTagId());
        }

        // 排序：置顶优先，然后按发布时间倒序
        wrapper.orderByDesc(BlogArticle::getIsTop, BlogArticle::getPublishTime);

        return wrapper;
    }

    /**
     * 转换为文章列表 VO
     */
    private Page<ArticleListVO> convertToArticleListVO(Page<BlogArticle> articlePage) {
        Page<ArticleListVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(),
                articlePage.getTotal());

        List<ArticleListVO> voList = articlePage.getRecords().stream().map(article -> {
            // 查询分类信息
            BlogCategory category = categoryMapper.selectById(article.getCategoryId());

            // 查询作者信息
            SysUser author = userMapper.selectById(article.getAuthorId());

            // 查询标签列表
            List<BlogTag> tags = tagMapper.selectTagsByArticleId(article.getId());
            List<ArticleListVO.TagInfo> tagInfos = tags.stream()
                    .map(tag -> ArticleListVO.TagInfo.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .color(tag.getColor())
                            .build())
                    .collect(Collectors.toList());

            return ArticleListVO.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .summary(article.getSummary())
                    .coverImg(article.getCoverImg())
                    .categoryId(article.getCategoryId())
                    .categoryName(category != null ? category.getName() : null)
                    .authorId(article.getAuthorId())
                    .authorName(author != null ? author.getNickname() : null)
                    .status(article.getStatus())
                    .isTop(article.getIsTop())
                    .views(article.getViews())
                    .tags(tagInfos)
                    .publishTime(article.getPublishTime())
                    .createTime(article.getCreateTime())
                    .build();
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long articleId, boolean isPublic) {
        ArticleDetailVO detailVO = null;

        // 如果是前台访问，先尝试从 Redis 缓存读取
        if (isPublic) {
            try {
                String cacheKey = ARTICLE_DETAIL_CACHE_PREFIX + articleId;
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached instanceof ArticleDetailVO) {
                    detailVO = (ArticleDetailVO) cached;
                    log.debug("从 Redis 缓存读取文章详情: articleId={}", articleId);
                }
            } catch (Exception e) {
                log.warn("Redis 读取文章缓存失败: articleId={}", articleId, e);
            }
        }

        // 缓存未命中，从数据库查询
        if (detailVO == null) {
            // 查询文章
            BlogArticle article = this.getById(articleId);
            if (article == null) {
                throw new BizException("文章不存在");
            }

            // 查询分类信息
            BlogCategory category = categoryMapper.selectById(article.getCategoryId());

            // 查询作者信息
            SysUser author = userMapper.selectById(article.getAuthorId());

            // 查询标签列表
            List<BlogTag> tags = tagMapper.selectTagsByArticleId(articleId);
            List<ArticleDetailVO.TagInfo> tagInfos = tags.stream()
                    .map(tag -> ArticleDetailVO.TagInfo.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .color(tag.getColor())
                            .build())
                    .collect(Collectors.toList());

            // 查询上一篇和下一篇文章
            ArticleDetailVO.ArticleNavigation prevArticle = getPrevArticle(articleId, article.getCategoryId());
            ArticleDetailVO.ArticleNavigation nextArticle = getNextArticle(articleId, article.getCategoryId());

            // 构建 VO
            detailVO = ArticleDetailVO.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .summary(article.getSummary())
                    .content(article.getContent())
                    .coverImg(article.getCoverImg())
                    .category(category != null ? ArticleDetailVO.CategoryInfo.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .build() : null)
                    .author(author != null ? ArticleDetailVO.AuthorInfo.builder()
                            .id(author.getId())
                            .nickname(author.getNickname())
                            .avatar(author.getAvatar())
                            .build() : null)
                    .status(article.getStatus())
                    .isTop(article.getIsTop())
                    .views(article.getViews())
                    .tags(tagInfos)
                    .prevArticle(prevArticle)
                    .nextArticle(nextArticle)
                    .publishTime(article.getPublishTime())
                    .createTime(article.getCreateTime())
                    .updateTime(article.getUpdateTime())
                    .build();

            // 如果是前台访问，将详情写入 Redis 缓存
            if (isPublic) {
                try {
                    String cacheKey = ARTICLE_DETAIL_CACHE_PREFIX + articleId;
                    redisTemplate.opsForValue().set(cacheKey, detailVO, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                    log.debug("文章详情写入 Redis 缓存: articleId={}", articleId);
                } catch (Exception e) {
                    log.warn("Redis 写入文章缓存失败: articleId={}", articleId, e);
                }
            }
        }

        // 如果是前台访问，增加阅读量（使用 Redis 计数器）
        if (isPublic) {
            incrementViewsInRedis(articleId);
            // 更新 VO 中的阅读量（显示最新值）
            Integer currentViews = getCurrentViews(articleId);
            detailVO.setViews(currentViews);
        }

        return detailVO;
    }

    /**
     * 在 Redis 中增加阅读量（原子操作，防刷）
     * 使用 Redis 的 incr 命令，高性能且线程安全
     */
    private void incrementViewsInRedis(Long articleId) {
        try {
            String viewsKey = ARTICLE_VIEWS_CACHE_PREFIX + articleId;
            Long currentViews = redisTemplate.opsForValue().increment(viewsKey);

            // 如果是第一次访问，需要从数据库加载初始值
            if (currentViews != null && currentViews == 1) {
                BlogArticle article = this.getById(articleId);
                if (article != null && article.getViews() != null && article.getViews() > 0) {
                    // 重置为数据库中的值 + 1
                    redisTemplate.opsForValue().set(viewsKey, article.getViews() + 1);
                }
            }

            log.debug("文章阅读量 +1 (Redis): articleId={}, currentViews={}", articleId, currentViews);
        } catch (Exception e) {
            log.error("增加文章阅读量失败 (Redis): articleId={}", articleId, e);
        }
    }

    /**
     * 获取当前阅读量（优先从 Redis 读取）
     */
    private Integer getCurrentViews(Long articleId) {
        try {
            String viewsKey = ARTICLE_VIEWS_CACHE_PREFIX + articleId;
            Object views = redisTemplate.opsForValue().get(viewsKey);
            if (views != null) {
                return Integer.parseInt(views.toString());
            }
        } catch (Exception e) {
            log.warn("从 Redis 读取阅读量失败: articleId={}", articleId, e);
        }

        // Redis 读取失败，降级查数据库
        BlogArticle article = this.getById(articleId);
        return article != null ? article.getViews() : 0;
    }

    /**
     * 获取上一篇文章
     */
    private ArticleDetailVO.ArticleNavigation getPrevArticle(Long currentId, Long categoryId) {
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogArticle::getStatus, 1)
                .eq(BlogArticle::getCategoryId, categoryId)
                .lt(BlogArticle::getId, currentId)
                .orderByDesc(BlogArticle::getId)
                .last("LIMIT 1");

        BlogArticle prevArticle = this.getOne(wrapper);
        if (prevArticle != null) {
            return ArticleDetailVO.ArticleNavigation.builder()
                    .id(prevArticle.getId())
                    .title(prevArticle.getTitle())
                    .build();
        }
        return null;
    }

    /**
     * 获取下一篇文章
     */
    private ArticleDetailVO.ArticleNavigation getNextArticle(Long currentId, Long categoryId) {
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogArticle::getStatus, 1)
                .eq(BlogArticle::getCategoryId, categoryId)
                .gt(BlogArticle::getId, currentId)
                .orderByAsc(BlogArticle::getId)
                .last("LIMIT 1");

        BlogArticle nextArticle = this.getOne(wrapper);
        if (nextArticle != null) {
            return ArticleDetailVO.ArticleNavigation.builder()
                    .id(nextArticle.getId())
                    .title(nextArticle.getTitle())
                    .build();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long articleId) {
        // 修复缓存一致性：删除前先清除缓存
        clearArticleCache(articleId);

        // 逻辑删除文章
        boolean removed = this.removeById(articleId);
        if (!removed) {
            throw new BizException("删除文章失败");
        }

        // 删除文章标签关联
        LambdaQueryWrapper<BlogArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogArticleTag::getArticleId, articleId);
        articleTagMapper.delete(wrapper);

        log.info("文章删除成功：articleId={}", articleId);
    }

    /**
     * 清除文章缓存
     * 修复缓存一致性问题：在更新或删除文章后调用此方法
     * 完善缓存清理逻辑：同时清除详情缓存和阅读量缓存，使用批量删除提高性能
     *
     * @param articleId 文章ID
     */
    private void clearArticleCache(Long articleId) {
        try {
            // 收集需要删除的 Key
            List<String> keys = new ArrayList<>();
            keys.add(ARTICLE_DETAIL_CACHE_PREFIX + articleId); // 详情缓存 blog:article:{id}
            keys.add(ARTICLE_VIEWS_CACHE_PREFIX + articleId); // 阅读量缓存 blog:views:{id}

            // 批量删除
            Long count = redisTemplate.delete(keys);

            if (count != null && count > 0) {
                log.debug("清除文章相关缓存成功: articleId={}, deletedCount={}", articleId, count);
            } else {
                log.debug("无需清除文章缓存: articleId={}", articleId);
            }
        } catch (Exception e) {
            log.warn("清除文章缓存失败: articleId={}", articleId, e);
        }
    }
}
