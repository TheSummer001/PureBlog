package com.github.toran.module.system.controller;

import com.github.toran.common.core.domain.Result;
import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.module.system.dto.LoginBody;
import com.github.toran.module.system.dto.LoginVo;
import com.github.toran.module.system.dto.UserInfoVo;
import com.github.toran.module.system.service.IAuthService;
import com.github.toran.module.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 *
 * @author toran
 */
@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息等接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final ISysUserService sysUserService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginBody loginBody) {
        LoginVo loginVo = authService.login(loginBody);
        return Result.success(loginVo);
    }

    @Operation(summary = "用户登出", description = "清除用户登录状态")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("登出成功");
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息（包含角色和权限）")
    @GetMapping("/info")
    public Result<UserInfoVo> getInfo() {
        // 从 SecurityContext 中获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return Result.error(401, "未登录或登录已过期");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        UserInfoVo userInfo = sysUserService.getUserInfo(loginUser.getUserId());

        return Result.success(userInfo);
    }
}
