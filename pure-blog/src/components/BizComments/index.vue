<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Giscus from '@giscus/vue'
import request from '@/utils/request'

// Giscus 配置
const giscusConfig = ref<{
  repo: string
  repoId: string
  category: string
  categoryId: string
  mapping: string
} | null>(null)

// 加载状态
const loading = ref(true)
const error = ref(false)

// 获取 Giscus 配置
const loadGiscusConfig = async () => {
  try {
    const response: any = await request.get('/public/config')
    const config = response.data || response
    const giscus = config.giscus
    
    if (giscus && giscus.repo && giscus.repoId && giscus.categoryId) {
      giscusConfig.value = {
        repo: giscus.repo,
        repoId: giscus.repoId,
        category: giscus.category || 'General',
        categoryId: giscus.categoryId,
        mapping: giscus.mapping || 'pathname'
      }
    } else {
      error.value = true
    }
  } catch (err) {
    console.error('Failed to load Giscus config:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  loadGiscusConfig()
})
</script>

<template>
  <div class="biz-comments">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      正在加载评论组件...
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      评论组件加载失败
    </div>
    
    <!-- Giscus 组件 -->
    <Giscus
      v-else-if="giscusConfig"
      :repo="giscusConfig.repo"
      :repo-id="giscusConfig.repoId"
      :category="giscusConfig.category"
      :category-id="giscusConfig.categoryId"
      :mapping="giscusConfig.mapping"
      :reactions-enabled="true"
      :emit-metadata="false"
      :input-position="'top'"
      :theme="'preferred_color_scheme'"
      :lang="'zh-CN'"
      :loading="'lazy'"
    />
  </div>
</template>

<style scoped>
.biz-comments {
  margin-top: 2rem;
}

.loading, .error {
  padding: 1rem;
  text-align: center;
  color: #666;
}
</style>