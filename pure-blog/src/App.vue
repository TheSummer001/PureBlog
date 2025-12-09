<script setup lang="ts">
import { onMounted } from 'vue'
import { NConfigProvider, NGlobalStyle } from 'naive-ui'
import { useAppStore } from '@/store/app'

// 使用应用状态管理
const appStore = useAppStore()

// 初始化主题
onMounted(() => {
  appStore.initTheme()
})
</script>

<template>
  <NConfigProvider :theme="appStore.naiveTheme">
    <NGlobalStyle />
    <router-view />
  </NConfigProvider>
</template>

<style scoped>
/* 全局样式可以放在 style.css 中 */
</style>