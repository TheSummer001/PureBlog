<script setup lang="ts">
import { useAppStore } from '@/store/app'
import { NButton, NSpace } from 'naive-ui'
import { computed } from 'vue'

const appStore = useAppStore()

// 计算属性：当前主题图标
const themeIcon = computed(() => {
  return appStore.theme === 'light' ? '☀️' : '🌙'
})

// 计算属性：当前主题文本
const themeText = computed(() => {
  return appStore.theme === 'light' ? 'Dark' : 'Light'
})

// 切换主题
const toggleTheme = () => {
  appStore.toggleTheme()
}
</script>

<template>
  <NButton @click="toggleTheme" circle>
    {{ themeIcon }} {{ themeText }}
  </NButton>
</template>

<style scoped>
/* 可以在这里添加自定义样式 */
</style>