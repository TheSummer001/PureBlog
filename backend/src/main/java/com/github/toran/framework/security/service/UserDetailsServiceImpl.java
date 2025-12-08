package com.github.toran.framework.security.service;

import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.module.system.entity.SysUser;
import com.github.toran.module.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 * Spring Security 使用此类加载用户信息
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ISysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            log.warn("用户不存在：{}", username);
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("用户已被禁用：{}", username);
            throw new UsernameNotFoundException("用户已被禁用：" + username);
        }

        // 构建登录用户对象
        return sysUserService.buildLoginUser(user);
    }
}
