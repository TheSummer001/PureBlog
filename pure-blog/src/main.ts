// src/main.ts
import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 导入 router
import router from './router'
// 【改动点】导入我们刚才改写的守卫函数
import { setupRouterGuard } from './router/guard'

const app = createApp(App)

// 配置 Pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// 配置 Router
app.use(router)

// 初始化路由守卫（必须在 use(router) 之后）
setupRouterGuard(router)

app.mount('#app')