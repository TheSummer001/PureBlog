package com.github.toran.framework.security.util;

import cn.hutool.core.date.DateUtil;
import com.github.toran.framework.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析、验证
 *
 * @author toran
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    /**
     * 获取签名密钥
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 生成 Token（带额外声明）
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @param claims 额外声明
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return Long.parseLong(claims.getSubject());
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getExpiration();
        }
        return null;
    }

    /**
     * 从 Token 中获取所有声明
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 Token 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return false;
            }
            
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration != null && !expiration.before(new Date());
        } catch (Exception e) {
            log.error("验证 Token 失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return null;
            }
            
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            
            return generateToken(userId, username);
        } catch (Exception e) {
            log.error("刷新 Token 失败：{}", e.getMessage());
            return null;
        }
    }
}
