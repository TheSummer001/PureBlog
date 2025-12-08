package com.github.toran.module.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.toran.module.content.entity.BlogTag;
import com.github.toran.module.content.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author toran
 */
public interface ITagService extends IService<BlogTag> {

    /**
     * 查询所有启用的标签
     *
     * @return 标签列表
     */
    List<TagVO> listAllEnabled();

    /**
     * 创建标签
     *
     * @param name   标签名称
     * @param color  标签颜色
     * @param status 状态
     * @return 标签ID
     */
    Long createTag(String name, String color, Integer status);

    /**
     * 更新标签
     *
     * @param id     标签ID
     * @param name   标签名称
     * @param color  标签颜色
     * @param status 状态
     */
    void updateTag(Long id, String name, String color, Integer status);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);

    /**
     * 获取标签详情（包含文章数量）
     *
     * @param id 标签ID
     * @return 标签VO
     */
    TagVO getTagDetail(Long id);
}
