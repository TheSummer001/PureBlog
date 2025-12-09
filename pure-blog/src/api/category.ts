import request from '@/utils/request'
import type { CategoryVO } from '@/types/category'

// 创建分类
export const createCategory = (data: { name: string; description?: string; sort: number; status: number }) => {
    return request.post<number>('/admin/category', data)
}

// 更新分类
export const updateCategory = (id: number, data: { name: string; description?: string; sort: number; status: number }) => {
    return request.put<void>(`/admin/category/${id}`, data)
}

// 删除分类
export const deleteCategory = (id: number) => {
    return request.delete<void>(`/admin/category/${id}`)
}

// 获取分类列表
export const getCategoryList = () => {
    return request.get<CategoryVO[]>('/admin/category/list')
}

// 获取分类详情
export const getCategoryDetail = (id: number) => {
    return request.get<CategoryVO>(`/admin/category/${id}`)
}