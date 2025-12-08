package com.github.toran.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
