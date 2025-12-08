package com.github.toran.module.system.service;

import com.github.toran.framework.security.domain.LoginUser;
import com.github.toran.module.system.dto.UserInfoVo;
import com.github.toran.module.system.entity.SysUser;

public interface ISysUserService {
     SysUser getByUsername(String username);
     UserInfoVo getUserInfo(Long userId);
     LoginUser buildLoginUser(SysUser user);


}
