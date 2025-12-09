// src/api/dashboard.ts
import request from '@/utils/request'

// 统计数据接口定义
export interface DashboardStats {
    articleCount: number
    viewCount: number
    categoryCount: number
    tagCount: number
}

// 发布记录接口定义
export interface PublishRecord {
    date: string
    count: number
}

// 获取核心统计数据
export const getDashboardStats = () => {
    return request.get<DashboardStats>('/admin/dashboard/stats')
}

// 获取发布记录 (默认获取最近 365 天，用于热力图或趋势图)
export const getPublishRecord = (days: number = 365) => {
    return request.get<PublishRecord[]>('/admin/dashboard/publish-record', {
        params: { days }
    })
}