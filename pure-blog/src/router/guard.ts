// src/router/guard.ts
import type { Router } from 'vue-router' // 只导入类型
import { useUserStore } from '@/store/user'
import NProgress from 'nprogress'

// 白名单路径
const whiteList = ['/login', '/register', '/', '/article']

// 【改动点】导出一个函数，接收 router 实例作为参数
export function setupRouterGuard(router: Router) {
    // 全局前置守卫
    router.beforeEach(async (to, from, next) => {
        // 开始进度条
        NProgress.start()

        // 获取用户状态
        const userStore = useUserStore()

        // 简单的白名单放行逻辑
        if (whiteList.includes(to.path) || to.path.startsWith('/article/')) {
            next()
        } else if (to.path.startsWith('/admin')) {
            // 访问管理后台需要认证
            if (userStore.token) {
                next()
            } else {
                next(`/login?redirect=${to.fullPath}`)
            }
        } else {
            next()
        }
    })

    // 全局后置守卫
    router.afterEach(() => {
        NProgress.done()
    })
}