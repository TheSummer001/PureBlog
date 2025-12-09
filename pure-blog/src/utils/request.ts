import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/store/user'
import { createDiscreteApi } from 'naive-ui'

// 创建 axios 实例
const service = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 定义统一响应结构
export interface APIResult<T> {
    code: number
    message: string
    data: T
}

// 创建离散API用于全局消息提示
const { message } = createDiscreteApi(['message'])

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse<APIResult<any>>) => {
        const { code, message: msg, data } = response.data

        // 如果请求成功 (Code 200)，直接返回 data (业务数据)
        if (code === 200) {
            return data
        }

        // 全局错误处理
        message.error(msg || '请求失败')

        // 401 处理：Token 过期
        if (code === 401) {
            const userStore = useUserStore()
            userStore.logout()
            const currentRoute = window.location.hash.substring(1)
            window.location.href = `/#/login?redirect=${encodeURIComponent(currentRoute)}`
        }

        return Promise.reject(new Error(msg || '请求失败'))
    },
    (error) => {
        message.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

// ✅ 关键修复：重新定义 request 的类型，使其符合拦截器解包后的行为
// 这样在调用时，request.get<T> 返回的就是 Promise<T> 而不是 Promise<AxiosResponse<T>>
const request = {
    get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return service.get(url, config) as Promise<T>
    },
    post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return service.post(url, data, config) as Promise<T>
    },
    put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return service.put(url, data, config) as Promise<T>
    },
    delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return service.delete(url, config) as Promise<T>
    }
}

export default request