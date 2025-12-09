<template>
  <div class="article-detail-container">
    <n-skeleton v-if="loading" :sharp="false" />
    <template v-else>
      <div class="article-layout">
        <!-- 文章内容区域 -->
        <div class="article-main">
          <h1>{{ article.title }}</h1>
          <div class="meta">
            <span>👤 {{ article.author?.nickname || '未知作者' }}</span>
            <span>📅 {{ formatDate(article.publishTime) }}</span>
            <span>👁️ {{ article.views }} 阅读</span>
          </div>
          
          <!-- Markdown 内容预览 -->
          <div class="content">
            <MdPreview 
              :editorId="`preview-${article.id}`"
              :model-value="article.content" 
              style="background-color: transparent;"
              :code-theme="'github'"
            />
          </div>
          
          <!-- 评论区域 -->
          <div class="comments-section">
            <BizComments :key="`comments-${article.id}`" />
          </div>
        </div>
        
        <!-- 侧边栏目录 -->
        <div class="article-sidebar">
          <n-card title="目录" size="small">
            <MdCatalog 
              :editorId="`preview-${article.id}`"
              :scrollElement="scrollElement"
              :offsetTop="80"
            />
          </n-card>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPublicArticleDetail } from '@/api/article'
import type { ArticleDetailVO } from '@/types/article'
import { MdPreview, MdCatalog } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useMessage, NSkeleton, NCard } from 'naive-ui'
import BizComments from '@/components/BizComments/index.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()

// 文章数据
const article = ref<ArticleDetailVO>({
  id: '0',
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
  prevArticle: { id: '0', title: '' },
  nextArticle: { id: '0', title: '' },
  publishTime: '',
  createTime: '',
  updateTime: ''
})

const loading = ref(true)
const scrollElement = ref<HTMLElement | undefined>(undefined)

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
  const articleId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
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

// 设置滚动元素
onMounted(() => {
  fetchArticleDetail()
  // 获取滚动容器
  scrollElement.value = document.documentElement
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.article-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.article-layout {
  display: flex;
  gap: 24px;
}

.article-main {
  flex: 1;
  min-width: 0;
}

.article-sidebar {
  width: 250px;
  flex-shrink: 0;
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

.comments-section {
  margin-top: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-layout {
    flex-direction: column;
  }
  
  .article-sidebar {
    width: 100%;
  }
}
</style>