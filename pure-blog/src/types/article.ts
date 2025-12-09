// 文章查询条件 DTO
export interface ArticleQueryDTO {
    title?: string
    categoryId?: number
    tagId?: number
    status?: number
    isTop?: number
    pageNum?: number
    pageSize?: number
}

// 文章提交 DTO（新增/修改）
export interface ArticleSubmitDTO {
    id?: number
    title: string
    summary?: string
    content: string
    coverImg?: string
    categoryId: number
    status?: number
    isTop?: number
    tagIds?: number[]
}

// 文章列表 VO（不含 content 全文）
export interface ArticleListVO {
    id: number
    title: string
    summary: string
    coverImg: string
    categoryId: number
    categoryName: string
    authorId: number
    authorName: string
    status: number
    isTop: number
    views: number
    tags: TagInfo[]
    publishTime: string
    createTime: string
}

// 文章详情 VO（包含 content 全文）
export interface ArticleDetailVO {
    id: number
    title: string
    summary: string
    content: string
    coverImg: string
    category: CategoryInfo
    author: AuthorInfo
    status: number
    isTop: number
    views: number
    tags: TagInfo[]
    prevArticle: ArticleNavigation
    nextArticle: ArticleNavigation
    publishTime: string
    createTime: string
    updateTime: string
}

// 标签信息
export interface TagInfo {
    id: number
    name: string
    color: string
}

// 分类信息
export interface CategoryInfo {
    id: number
    name: string
}

// 作者信息
export interface AuthorInfo {
    id: number
    nickname: string
    avatar: string
}

// 文章导航（上一篇/下一篇）
export interface ArticleNavigation {
    id: number
    title: string
}