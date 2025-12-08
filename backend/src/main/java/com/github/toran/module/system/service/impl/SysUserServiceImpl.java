package com.github.toran.module.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.common.exception.BizException;
import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.module.system.dto.UserInfoVo;
import com.github.toran.module.system.entity.SysRole;
import com.github.toran.module.system.entity.SysUser;
import com.github.toran.module.system.mapper.SysRoleMapper;
import com.github.toran.module.system.mapper.SysUserMapper;
import com.github.toran.module.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysRoleMapper sysRoleMapper;

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public UserInfoVo getUserInfo(Long userId) {
        // 查询用户基本信息
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(userId);
        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());

        // TODO: 后续根据角色查询权限列表
        List<String> permissions = new ArrayList<>();

        return UserInfoVo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .roles(roleKeys)
                .permissions(permissions)
                .build();
    }

    @Override
    public LoginUser buildLoginUser(SysUser user) {
        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());

        log.info("用户 {} 的角色列表：{}", user.getUsername(), roleKeys);

        // TODO: 后续根据角色查询权限列表
        List<String> permissions = new ArrayList<>();

        // 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setStatus(user.getStatus());
        loginUser.setRoles(roleKeys);
        loginUser.setPermissions(permissions);

        log.info("构建的 LoginUser 角色：{}", loginUser.getRoles());

        return loginUser;
    }
}
