<template>
  <div class="article-detail">
    <n-skeleton v-if="loading" :sharp="false" />
    <template v-else>
      <h1>{{ article.title }}</h1>
      <div class="meta">
        <span>👤 {{ article.author?.nickname || '未知作者' }}</span>
        <span>📅 {{ formatDate(article.publishTime) }}</span>
        <span>👁️ {{ article.views }} 阅读</span>
      </div>
      <div class="content">
        <MdPreview 
          :model-value="article.content" 
          style="background-color: transparent;"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPublicArticleDetail } from '@/api/article'
import type { ArticleDetailVO } from '@/types/article'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useMessage, NSkeleton } from 'naive-ui'

const route = useRoute()
const router = useRouter()
const message = useMessage()

// 文章数据
const article = ref<ArticleDetailVO>({
  id: 0,
  title: '',
  summary: '',
  content: '',
  coverImg: '',
  category: { id: 0, name: '' },
  author: { id: 0, nickname: '', avatar: '' },
  status: 0,
  isTop: 0,
  views: 0,
  tags: [],
  prevArticle: { id: 0, title: '' },
  nextArticle: { id: 0, title: '' },
  publishTime: '',
  createTime: '',
  updateTime: ''
})

const loading = ref(true)

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 获取文章详情
const fetchArticleDetail = async () => {
  const articleId = Number(route.params.id)
  if (!articleId) {
    message.error('无效的文章ID')
    router.push('/')
    return
  }

  try {
    loading.value = true
    const data: ArticleDetailVO = await getPublicArticleDetail(articleId)
    article.value = data
  } catch (error) {
    message.error('获取文章详情失败')
    console.error(error)
    router.push('/')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchArticleDetail()
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