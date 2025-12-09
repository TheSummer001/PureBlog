import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import 'nprogress/nprogress.css'


// 布局组件
const PublicLayout = () => import('@/layout/PublicLayout.vue')
const AdminLayout = () => import('@/layout/AdminLayout.vue')

// 视图组件
const Login = () => import('@/views/login/index.vue')

// 路由表
const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { title: '登录' }
    },
    {
        path: '/',
        component: PublicLayout,
        children: [
            {
                path: '',
                name: 'Home',
                component: () => import('@/views/home/index.vue'),
                meta: { title: '首页' }
            },
            {
                path: 'article/:id',
                name: 'ArticleDetail',
                component: () => import('@/views/article/detail.vue'),
                meta: { title: '文章详情' }
            }
        ]
    },
    {
        path: '/admin',
        component: AdminLayout,
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                redirect: '/admin/dashboard'
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/admin/dashboard.vue'),
                meta: { title: '仪表盘' }
            },
            {
                path: 'articles',
                name: 'Articles',
                component: () => import('@/views/admin/articles.vue'),
                meta: { title: '文章管理' }
            },
            {
                path: 'article/edit/:id?',
                name: 'ArticleEdit',
                component: () => import('@/views/admin/article/edit.vue'),
                meta: { title: '文章编辑' }
            },
            {
                path: 'categories',
                name: 'Categories',
                component: () => import('@/views/admin/categories.vue'),
                meta: { title: '分类管理' }
            },
            {
                path: 'settings',
                name: 'Settings',
                component: () => import('@/views/admin/settings.vue'),
                meta: { title: '系统设置' }
            }
        ]
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router