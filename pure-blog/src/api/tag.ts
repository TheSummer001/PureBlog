import request from '@/utils/request'
import type { TagVO } from '@/types/tag'

// 创建标签
export const createTag = (data: { name: string; color?: string; status: number }) => {
    return request.post<number>('/admin/tag', data)
}

// 更新标签
export const updateTag = (id: number, data: { name: string; color?: string; status: number }) => {
    return request.put<void>(`/admin/tag/${id}`, data)
}

// 删除标签
export const deleteTag = (id: number) => {
    return request.delete<void>(`/admin/tag/${id}`)
}

// 获取标签列表
export const getTagList = () => {
    return request.get<TagVO[]>('/admin/tag/list')
}

// 获取标签详情
export const getTagDetail = (id: number) => {
    return request.get<TagVO>(`/admin/tag/${id}`)
}