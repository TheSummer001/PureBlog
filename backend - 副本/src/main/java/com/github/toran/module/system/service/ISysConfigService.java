package com.github.toran.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.toran.module.system.entity.SysConfig;
import com.github.toran.module.system.vo.BlogConfigVO;

/**
 * 系统配置服务接口
 * 核心功能：动态读取配置，支持 Redis 缓存
 *
 * @author toran
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 保存或更新配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @param description 配置描述
     * @param configGroup 配置分组
     * @return 是否成功
     */
    boolean saveOrUpdateConfig(String configKey, String configValue, String description, String configGroup);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    boolean deleteConfig(String configKey);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 获取博客公开配置（Giscus + 站点信息）
     *
     * @return 博客配置
     */
    BlogConfigVO getBlogConfig();
}
