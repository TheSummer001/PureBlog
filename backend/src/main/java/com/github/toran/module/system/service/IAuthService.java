package com.github.toran.module.system.service;

import com.github.toran.module.system.dto.LoginBody;
import com.github.toran.module.system.dto.LoginVo;

/**
 * 认证服务接口
 *
 * @author toran
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param loginBody 登录参数
     * @return 登录结果（包含 Token）
     */
    LoginVo login(LoginBody loginBody);

    /**
     * 用户登出
     */
    void logout();
}
