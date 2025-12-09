import { defineStore } from 'pinia'
import { darkTheme } from 'naive-ui'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    // 主题状态：'light' | 'dark'
    const theme = ref<'light' | 'dark'>('light')

    // Naive UI 主题
    const naiveTheme = ref<any>(null)

    // 切换主题
    const toggleTheme = () => {
        theme.value = theme.value === 'light' ? 'dark' : 'light'
        naiveTheme.value = theme.value === 'dark' ? darkTheme : null
    }

    // 初始化主题
    const initTheme = () => {
        const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null
        if (savedTheme) {
            theme.value = savedTheme
            naiveTheme.value = savedTheme === 'dark' ? darkTheme : null
        } else {
            // 如果没有保存的主题设置，根据系统偏好设置
            const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches
            theme.value = isDark ? 'dark' : 'light'
            naiveTheme.value = isDark ? darkTheme : null
        }
    }

    return {
        theme,
        naiveTheme,
        toggleTheme,
        initTheme
    }
}, {
    persist: true
})