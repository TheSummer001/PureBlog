package com.github.toran.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步任务配置
 * 启用 @Async 注解支持
 *
 * @author toran
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
