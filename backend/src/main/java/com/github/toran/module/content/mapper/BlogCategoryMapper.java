package com.github.toran.module.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.content.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {
}
