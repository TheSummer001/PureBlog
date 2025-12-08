package com.github.toran.module.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.toran.module.content.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章标签 Mapper 接口
 *
 * @author toran
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {

    /**
     * 根据文章 ID 查询标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    @Select("SELECT t.* FROM blog_tag t " +
            "INNER JOIN blog_article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId} AND t.deleted = 0")
    List<BlogTag> selectTagsByArticleId(@Param("articleId") Long articleId);
}
