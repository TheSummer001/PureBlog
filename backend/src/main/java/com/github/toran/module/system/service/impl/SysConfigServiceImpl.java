package com.github.toran.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.module.system.entity.SysConfig;
import com.github.toran.module.system.mapper.SysConfigMapper;
import com.github.toran.module.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务实现
 * 支持动态配置读取，预留 Redis 缓存接口
 *
 * @author toran
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    // TODO: 注入 Redis 缓存服务（可选）
    // private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        if (StrUtil.isBlank(configKey)) {
            return defaultValue;
        }

        // TODO: 优先从 Redis 缓存读取
        // String cachedValue = redisTemplate.opsForValue().get("sys:config:" + configKey);
        // if (StrUtil.isNotBlank(cachedValue)) {
        //     return cachedValue;
        // }

        // 从数据库查询
        SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .last("LIMIT 1"));

        if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
            // TODO: 写入 Redis 缓存
            // redisTemplate.opsForValue().set("sys:config:" + configKey, config.getConfigValue());
            return config.getConfigValue();
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
                // TODO: 更新 Redis 缓存
                // redisTemplate.opsForValue().set("sys:config:" + configKey, configValue);
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
                // TODO: 写入 Redis 缓存
                // redisTemplate.opsForValue().set("sys:config:" + configKey, configValue);
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
            // TODO: 删除 Redis 缓存
            // redisTemplate.delete("sys:config:" + configKey);
        }

        return result;
    }

    @Override
    public void refreshCache() {
        log.info("刷新配置缓存...");
        
        // TODO: 清空 Redis 缓存，重新加载所有配置
        // redisTemplate.delete(redisTemplate.keys("sys:config:*"));
        
        log.info("配置缓存刷新完成");
    }
}
