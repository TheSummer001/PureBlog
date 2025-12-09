// 分类 VO
export interface CategoryVO {
    id: number
    name: string
    description: string
    sort: number
    status: number
    articleCount?: number
    createTime: string
}