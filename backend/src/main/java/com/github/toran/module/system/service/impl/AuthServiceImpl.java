package com.github.toran.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.toran.common.constant.ResultCode;
import com.github.toran.common.exception.BizException;
import com.github.toran.framework.security.config.JwtProperties;
import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.framework.security.util.JwtTokenUtil;
import com.github.toran.module.system.dto.LoginBody;
import com.github.toran.module.system.dto.LoginVo;
import com.github.toran.module.system.entity.SysUser;
import com.github.toran.module.system.service.IAuthService;
import com.github.toran.module.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final ISysUserService sysUserService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;

    @Override
    public LoginVo login(LoginBody loginBody) {
        // 参数校验
        if (StrUtil.isBlank(loginBody.getUsername()) || StrUtil.isBlank(loginBody.getPassword())) {
            throw new BizException(ResultCode.PARAM_VALID_ERROR, "用户名或密码不能为空");
        }

        // 查询用户
        SysUser user = sysUserService.getByUsername(loginBody.getUsername());
        if (user == null) {
            log.warn("登录失败：用户不存在，username={}", loginBody.getUsername());
            throw new BizException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("登录失败：用户已被禁用，username={}", loginBody.getUsername());
            throw new BizException(ResultCode.FORBIDDEN, "用户已被禁用，请联系管理员");
        }

        // 使用 Spring Security 进行认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginBody.getUsername(), loginBody.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("登录失败：认证异常，username={}, error={}", loginBody.getUsername(), e.getMessage());
            throw new BizException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 构建 LoginUser 获取角色和权限信息
        LoginUser loginUser = sysUserService.buildLoginUser(user);

        // 生成 JWT Token（将角色信息存入 Token）
        String token = jwtTokenUtil.generateTokenWithRoles(user.getId(), user.getUsername(), loginUser.getRoles());
        Long expiration = System.currentTimeMillis() + jwtProperties.getExpiration();

        log.info("用户登录成功：username={}, userId={}, roles={}", user.getUsername(), user.getId(), loginUser.getRoles());

        // 返回登录结果
        return LoginVo.builder()
                .token(token)
                .expiration(expiration)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public void logout() {
        // 清除 Security 上下文
        SecurityContextHolder.clearContext();

        // TODO: 可选实现 Token 黑名单机制（使用 Redis 存储失效 Token）

        log.info("用户登出成功");
    }
}
