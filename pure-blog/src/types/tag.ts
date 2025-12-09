// 标签 VO
export interface TagVO {
    id: string | number
    name: string
    color: string
    status: number
    articleCount?: number
    createTime: string
}