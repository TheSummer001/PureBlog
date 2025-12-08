package com.github.toran.module.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.toran.module.content.entity.BlogCategory;
import com.github.toran.module.content.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author toran
 */
public interface ICategoryService extends IService<BlogCategory> {

    /**
     * 查询所有启用的分类（按 sort 排序）
     *
     * @return 分类列表
     */
    List<CategoryVO> listAllEnabled();

    /**
     * 创建分类
     *
     * @param name        分类名称
     * @param description 分类描述
     * @param sort        排序
     * @param status      状态
     * @return 分类ID
     */
    Long createCategory(String name, String description, Integer sort, Integer status);

    /**
     * 更新分类
     *
     * @param id          分类ID
     * @param name        分类名称
     * @param description 分类描述
     * @param sort        排序
     * @param status      状态
     */
    void updateCategory(Long id, String name, String description, Integer sort, Integer status);

    /**
     * 删除分类（需检查是否有文章关联）
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 获取分类详情（包含文章数量）
     *
     * @param id 分类ID
     * @return 分类VO
     */
    CategoryVO getCategoryDetail(Long id);
}
