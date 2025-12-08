package com.github.toran.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * @author toran
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     */
    private String secret = "personal-blog-secret-key-please-change-in-production";

    /**
     * JWT 过期时间（毫秒）
     * 默认 7 天
     */
    private Long expiration = 7 * 24 * 60 * 60 * 1000L;

    /**
     * JWT Token 前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * JWT Token Header 名称
     */
    private String header = "Authorization";
}
