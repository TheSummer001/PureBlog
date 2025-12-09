import request from '@/utils/request'
// 移除 APIResult 的导入，因为 request 内部处理了
import type {
    ArticleListVO,
    ArticleDetailVO,
    ArticleSubmitDTO,
    ArticleQueryDTO
} from '@/types/article'
import type { CategoryVO } from '@/types/category'
import type { TagVO } from '@/types/tag'
import type { Page } from '@/types/page'

// ✅ 修改：直接指定返回类型为 Page<ArticleListVO>
// request.ts 的 get 方法我们已经强转了，所以这里泛型传什么，await 出来的就是什么
// 但是实际上 axios 请求的是 APIResult<T>。
// 这里的最佳实践是：request.get 内部处理了 APIResult，所以这里我们告诉 TS 它返回的是核心数据。

export const getArticleList = (query: ArticleQueryDTO) => {
    // 这里的泛型是给 axios 的，axios 实际返回的是 Promise<Page<...>>
    return request.get<Page<ArticleListVO>>('/admin/article/list', { params: query })
}

export const getArticleDetail = (id: number) => {
    return request.get<ArticleDetailVO>(`/admin/article/${id}`)
}

export const saveArticle = (data: ArticleSubmitDTO) => {
    if (data.id) {
        return request.put<number>('/admin/article', data)
    } else {
        return request.post<number>('/admin/article', data)
    }
}

export const deleteArticle = (id: number) => {
    return request.delete<void>(`/admin/article/${id}`)
}

export const getCategoryList = () => {
    return request.get<CategoryVO[]>('/admin/category/list')
}

export const getTagList = () => {
    return request.get<TagVO[]>('/admin/tag/list')
}

// src/api/article.ts

// ... (保留原有的 import 和 admin 接口) ...

/**
 * ==========================================
 * 前台公开接口 (无需登录)
 * ==========================================
 */

// 获取公开文章列表
export const getPublicArticleList = (query: ArticleQueryDTO) => {
    return request.get<Page<ArticleListVO>>('/public/article/list', { params: query })
}

// 获取公开文章详情
export const getPublicArticleDetail = (id: number) => {
    return request.get<ArticleDetailVO>(`/public/article/${id}`)
}

// 获取热门文章
export const getHotArticles = () => {
    return request.get<Page<ArticleListVO>>('/public/article/hot')
}