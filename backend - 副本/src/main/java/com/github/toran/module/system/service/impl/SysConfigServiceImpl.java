package com.github.toran.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.module.system.entity.SysConfig;
import com.github.toran.module.system.mapper.SysConfigMapper;
import com.github.toran.module.system.service.ISysConfigService;
import com.github.toran.module.system.vo.BlogConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务实现
 * 支持动态配置读取，集成 Redis 缓存提升性能
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 缓存 Key 前缀
     */
    private static final String CONFIG_CACHE_PREFIX = "sys:config:";

    /**
     * 缓存过期时间（分钟）
     */
    private static final long CACHE_EXPIRE_MINUTES = 60;

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        if (StrUtil.isBlank(configKey)) {
            return defaultValue;
        }

        try {
            // 1. 优先从 Redis 缓存读取
            String cacheKey = CONFIG_CACHE_PREFIX + configKey;
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                log.debug("从 Redis 读取配置: {} = {}", configKey, cachedValue);
                return cachedValue.toString();
            }

            // 2. 缓存未命中，从数据库查询
            SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigKey, configKey)
                    .last("LIMIT 1"));

            if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
                String configValue = config.getConfigValue();
                // 3. 写入 Redis 缓存（设置过期时间）
                redisTemplate.opsForValue().set(cacheKey, configValue, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                log.debug("配置写入 Redis: {} = {}", configKey, configValue);
                return configValue;
            }
        } catch (Exception e) {
            // Redis 异常时降级为直接查数据库
            log.warn("Redis 读取失败，降级查询数据库: configKey={}", configKey, e);
            SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigKey, configKey)
                    .last("LIMIT 1"));
            if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
                return config.getConfigValue();
            }
        }

        return defaultValue;
    }

    @Override
    public boolean saveOrUpdateConfig(String configKey, String configValue, String description, String configGroup) {
        if (StrUtil.isBlank(configKey)) {
            return false;
        }

        SysConfig existingConfig = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .last("LIMIT 1"));

        if (existingConfig != null) {
            // 更新
            existingConfig.setConfigValue(configValue);
            existingConfig.setDescription(description);
            existingConfig.setConfigGroup(configGroup);
            boolean result = this.updateById(existingConfig);

            if (result) {
                // 删除缓存，下次查询时重新加载
                String cacheKey = CONFIG_CACHE_PREFIX + configKey;
                redisTemplate.delete(cacheKey);
                log.info("配置更新成功，已删除缓存: {}", configKey);
            }

            return result;
        } else {
            // 新增
            SysConfig newConfig = new SysConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setDescription(description);
            newConfig.setConfigGroup(configGroup);
            newConfig.setIsEncrypted(0);

            boolean result = this.save(newConfig);

            if (result) {
                log.info("配置新增成功: {}", configKey);
            }

            return result;
        }
    }

    @Override
    public boolean deleteConfig(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return false;
        }

        boolean result = this.remove(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));

        if (result) {
            // 删除 Redis 缓存
            String cacheKey = CONFIG_CACHE_PREFIX + configKey;
            redisTemplate.delete(cacheKey);
            log.info("配置删除成功，已清除缓存: {}", configKey);
        }

        return result;
    }

    @Override
    public void refreshCache() {
        log.info("开始刷新配置缓存...");

        try {
            // 清空 Redis 缓存，重新加载所有配置
            Set<String> keys = redisTemplate.keys(CONFIG_CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                Long deleted = redisTemplate.delete(keys);
                log.info("已清空 {} 条配置缓存", deleted);
            }
        } catch (Exception e) {
            log.error("刷新配置缓存失败", e);
        }

        log.info("配置缓存刷新完成");
    }

    @Override
    public BlogConfigVO getBlogConfig() {
        // 构建站点信息
        BlogConfigVO.SiteInfo siteInfo = BlogConfigVO.SiteInfo.builder()
                .name(getConfigValue("site.name", "个人博客"))
                .description(getConfigValue("site.description", "分享技术与生活"))
                .icp(getConfigValue("site.icp", ""))
                .build();

        // 构建 Giscus 配置
        BlogConfigVO.GiscusConfig giscusConfig = BlogConfigVO.GiscusConfig.builder()
                .repo(getConfigValue("giscus.repo", ""))
                .repoId(getConfigValue("giscus.repo_id", ""))
                .category(getConfigValue("giscus.category", "Announcements"))
                .categoryId(getConfigValue("giscus.category_id", ""))
                .mapping(getConfigValue("giscus.mapping", "pathname"))
                .build();

        return BlogConfigVO.builder()
                .site(siteInfo)
                .giscus(giscusConfig)
                .build();
    }
}
