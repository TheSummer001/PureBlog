package com.github.toran.module.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.common.exception.BizException;
import com.github.toran.module.content.entity.BlogArticle;
import com.github.toran.module.content.entity.BlogCategory;
import com.github.toran.module.content.mapper.BlogArticleMapper;
import com.github.toran.module.content.mapper.BlogCategoryMapper;
import com.github.toran.module.content.service.ICategoryService;
import com.github.toran.module.content.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory> implements ICategoryService {

    private final BlogArticleMapper articleMapper;

    @Override
    public List<CategoryVO> listAllEnabled() {
        LambdaQueryWrapper<BlogCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogCategory::getStatus, 1)
                .orderByAsc(BlogCategory::getSort);

        List<BlogCategory> categories = this.list(wrapper);

        return categories.stream().map(category -> {
            // 统计该分类下的文章数量
            Long count = articleMapper.selectCount(
                    new LambdaQueryWrapper<BlogArticle>()
                            .eq(BlogArticle::getCategoryId, category.getId())
                            .eq(BlogArticle::getStatus, 1) // 只统计已发布的文章
            );

            return CategoryVO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .sort(category.getSort())
                    .status(category.getStatus())
                    .articleCount(count.intValue())
                    .createTime(category.getCreateTime())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Long createCategory(String name, String description, Integer sort, Integer status) {
        // 检查分类名称是否重复
        Long count = this.count(new LambdaQueryWrapper<BlogCategory>().eq(BlogCategory::getName, name));
        if (count > 0) {
            throw new BizException("分类名称已存在");
        }

        BlogCategory category = new BlogCategory();
        category.setName(name);
        category.setDescription(description);
        category.setSort(sort);
        category.setStatus(status);

        this.save(category);
        log.info("创建分类成功：id={}, name={}", category.getId(), name);

        return category.getId();
    }

    @Override
    public void updateCategory(Long id, String name, String description, Integer sort, Integer status) {
        BlogCategory category = this.getById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }

        // 检查分类名称是否重复（排除自己）
        Long count = this.count(
                new LambdaQueryWrapper<BlogCategory>()
                        .eq(BlogCategory::getName, name)
                        .ne(BlogCategory::getId, id)
        );
        if (count > 0) {
            throw new BizException("分类名称已存在");
        }

        category.setName(name);
        category.setDescription(description);
        category.setSort(sort);
        category.setStatus(status);

        this.updateById(category);
        log.info("更新分类成功：id={}, name={}", id, name);
    }

    @Override
    public void deleteCategory(Long id) {
        BlogCategory category = this.getById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }

        // 检查该分类下是否有文章
        Long articleCount = articleMapper.selectCount(
                new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getCategoryId, id)
        );
        if (articleCount > 0) {
            throw new BizException("该分类下还有 " + articleCount + " 篇文章，无法删除");
        }

        this.removeById(id);
        log.info("删除分类成功：id={}, name={}", id, category.getName());
    }

    @Override
    public CategoryVO getCategoryDetail(Long id) {
        BlogCategory category = this.getById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }

        // 统计该分类下的文章数量
        Long count = articleMapper.selectCount(
                new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getCategoryId, id)
        );

        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .sort(category.getSort())
                .status(category.getStatus())
                .articleCount(count.intValue())
                .createTime(category.getCreateTime())
                .build();
    }
}
