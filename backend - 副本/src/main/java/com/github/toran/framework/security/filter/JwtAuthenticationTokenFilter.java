package com.github.toran.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import com.github.toran.framework.security.config.JwtProperties;
import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.framework.security.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器
 * 拦截请求，验证 JWT Token，并设置用户认证信息
 *
 * @author toran
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头中的 Token
        String token = getTokenFromRequest(request);

        if (StrUtil.isNotBlank(token) && jwtTokenUtil.validateToken(token)) {
            // 从 Token 中获取用户信息
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            List<String> roles = jwtTokenUtil.getRolesFromToken(token);

            if (userId != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 构建登录用户信息
                LoginUser loginUser = new LoginUser();
                loginUser.setUserId(userId);
                loginUser.setUsername(username);
                loginUser.setStatus(1);
                loginUser.setRoles(roles != null ? roles : new ArrayList<>());
                loginUser.setPermissions(new ArrayList<>());

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser,
                        null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("用户 {} 认证成功，角色：{}", username, roles);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());

        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith(jwtProperties.getTokenPrefix())) {
            return bearerToken.substring(jwtProperties.getTokenPrefix().length());
        }

        return null;
    }
}
