package com.github.toran.module.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应数据
 *
 * @author toran
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token 过期时间（毫秒时间戳）
     */
    private Long expiration;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}
