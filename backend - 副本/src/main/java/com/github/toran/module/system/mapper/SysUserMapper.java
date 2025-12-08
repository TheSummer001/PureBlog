package com.github.toran.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
