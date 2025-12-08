package com.github.toran.module.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.content.entity.BlogArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章标签关联 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface BlogArticleTagMapper extends BaseMapper<BlogArticleTag> {
}
