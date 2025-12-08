package com.github.toran.module.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.toran.common.exception.BizException;
import com.github.toran.module.content.entity.BlogTag;
import com.github.toran.module.content.mapper.BlogArticleTagMapper;
import com.github.toran.module.content.mapper.BlogTagMapper;
import com.github.toran.module.content.service.ITagService;
import com.github.toran.module.content.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 *
 * @author toran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements ITagService {

    private final BlogArticleTagMapper articleTagMapper;

    @Override
    public List<TagVO> listAllEnabled() {
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogTag::getStatus, 1)
                .orderByDesc(BlogTag::getCreateTime);

        List<BlogTag> tags = this.list(wrapper);

        return tags.stream().map(tag -> TagVO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .status(tag.getStatus())
                .createTime(tag.getCreateTime())
                .build()).collect(Collectors.toList());
    }

    @Override
    public Long createTag(String name, String color, Integer status) {
        // 检查标签名称是否重复
        Long count = this.count(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getName, name));
        if (count > 0) {
            throw new BizException("标签名称已存在");
        }

        BlogTag tag = new BlogTag();
        tag.setName(name);
        tag.setColor(color != null ? color : "#1890ff"); // 默认颜色
        tag.setStatus(status);

        this.save(tag);
        log.info("创建标签成功：id={}, name={}", tag.getId(), name);

        return tag.getId();
    }

    @Override
    public void updateTag(Long id, String name, String color, Integer status) {
        BlogTag tag = this.getById(id);
        if (tag == null) {
            throw new BizException("标签不存在");
        }

        // 检查标签名称是否重复（排除自己）
        Long count = this.count(
                new LambdaQueryWrapper<BlogTag>()
                        .eq(BlogTag::getName, name)
                        .ne(BlogTag::getId, id));
        if (count > 0) {
            throw new BizException("标签名称已存在");
        }

        tag.setName(name);
        tag.setColor(color);
        tag.setStatus(status);

        this.updateById(tag);
        log.info("更新标签成功：id={}, name={}", id, name);
    }

    @Override
    public void deleteTag(Long id) {
        BlogTag tag = this.getById(id);
        if (tag == null) {
            throw new BizException("标签不存在");
        }

        this.removeById(id);
        log.info("删除标签成功：id={}, name={}", id, tag.getName());
    }

    @Override
    public TagVO getTagDetail(Long id) {
        BlogTag tag = this.getById(id);
        if (tag == null) {
            throw new BizException("标签不存在");
        }

        return TagVO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .status(tag.getStatus())
                .createTime(tag.getCreateTime())
                .build();
    }
}
