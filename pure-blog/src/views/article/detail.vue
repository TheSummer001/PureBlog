<template>
  <div class="article-detail">
    <h1>文章标题</h1>
    <div class="meta">
      <span>作者：张三</span>
      <span>发布时间：2025-01-01</span>
      <span>阅读量：100</span>
    </div>
    <div class="content">
      <p>这是文章内容...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'

const route = useRoute()

onMounted(() => {
  console.log('文章ID:', route.params.id)
})
</script>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.meta {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  color: #666;
}

.content {
  line-height: 1.8;
  font-size: 1.1rem;
}
</style>