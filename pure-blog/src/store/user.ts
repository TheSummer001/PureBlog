import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

// 用户信息接口
export interface UserInfo {
    userId: number
    username: string
    nickname: string
    avatar?: string
    roles: string[]
    permissions: string[]
}

// 登录请求参数
export interface LoginRequest {
    username: string
    password: string
}

// 登录响应
export interface LoginResponse {
    token: string
    expiration: number
    userId: number
    username: string
    nickname: string
    avatar?: string
}

export const useUserStore = defineStore('user', () => {
    // 用户令牌
    const token = ref<string>('')

    // 用户信息
    const userInfo = ref<UserInfo | null>(null)

    // 设置令牌
    const setToken = (newToken: string) => {
        token.value = newToken
    }

    // 设置用户信息
    const setUserInfo = (info: UserInfo) => {
        userInfo.value = info
    }

    // 登录
    // 登录
    const login = async (loginData: LoginRequest): Promise<LoginResponse> => {
        // request.post 返回的直接就是 LoginResponse 类型的数据
        const res = await request.post<LoginResponse>('/auth/login', loginData)

        // 直接从 res 中获取 token
        setToken(res.token)

        return res
    }

    // 获取用户信息
    // 获取用户信息
    const getUserInfo = async (): Promise<UserInfo> => {
        const res = await request.get<UserInfo>('/auth/info')
        setUserInfo(res) // 直接使用 res，不要 res.data
        return res
    }

    // 登出
    const logout = () => {
        token.value = ''
        userInfo.value = null
    }

    // 检查是否已登录
    const isLoggedIn = () => {
        return !!token.value
    }

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        login,
        getUserInfo,
        logout,
        isLoggedIn
    }
}, {
    persist: true
})