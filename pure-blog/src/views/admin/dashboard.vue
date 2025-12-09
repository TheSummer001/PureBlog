<template>
  <div class="dashboard-container">
    <n-card :bordered="false" class="header-section" content-style="padding: 0;">
      <div class="header-content">
        <div class="user-info">
          <n-avatar
            round
            :size="64"
            :src="userStore.userInfo?.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${userStore.userInfo?.username}`"
          />
          <div class="welcome-text">
            <div class="greeting">
              早安，{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}，开始您的一天吧！
            </div>
            <div class="role-info">
              <span class="role-tag">
                {{ userStore.userInfo?.roles?.[0] || '管理员' }}
              </span>
              <span class="weather-info">今日天气晴朗，适合写代码</span>
            </div>
          </div>
        </div>
        <div class="header-stats">
          <n-grid :cols="3" x-gap="24">
            <n-grid-item>
              <n-statistic label="文章总数">
                {{ stats.articleCount }}
              </n-statistic>
            </n-grid-item>
            <n-grid-item>
              <n-statistic label="分类/标签">
                {{ stats.categoryCount }} / {{ stats.tagCount }}
              </n-statistic>
            </n-grid-item>
            <n-grid-item>
              <n-statistic label="总阅读量">
                {{ stats.viewCount }}
              </n-statistic>
            </n-grid-item>
          </n-grid>
        </div>
      </div>
    </n-card>

    <n-grid :x-gap="24" :y-gap="24" cols="1 l:24" item-responsive responsive="screen" class="main-content mt-4">
      <n-grid-item span="24 l:16">
        <n-space vertical size="large">
          <n-card :bordered="false" title="近期文章" size="small" segmented>
            <template #header-extra>
              <n-button text type="primary" @click="router.push('/admin/articles')">全部文章</n-button>
            </template>
            
            <n-grid :x-gap="16" :y-gap="16" cols="1 s:2 m:3" responsive="screen">
              <n-grid-item v-for="item in recentArticles" :key="item.id">
                <n-card class="project-card" hoverable :bordered="true" size="small" @click="router.push(`/article/${item.id}`)">
                  <template #header>
                    <div class="project-header">
                      <n-avatar 
                        :src="item.coverImg" 
                        :fallback-src="`https://api.dicebear.com/7.x/shapes/svg?seed=${item.title}`"
                        size="small" 
                        class="mr-2"
                        style="background-color: transparent;"
                      />
                      <n-ellipsis class="project-title">{{ item.title }}</n-ellipsis>
                    </div>
                  </template>
                  <div class="project-desc">
                    <n-ellipsis :line-clamp="2" :tooltip="true">
                      {{ item.summary || '暂无摘要...' }}
                    </n-ellipsis>
                  </div>
                  <div class="project-footer mt-4">
                    <span class="author">{{ item.categoryName || '未分类' }}</span>
                    <span class="time">{{ formatDate(item.createTime) }}</span>
                  </div>
                </n-card>
              </n-grid-item>
              
              <template v-if="loading">
                <n-grid-item v-for="i in 6" :key="i">
                  <n-card size="small"><n-skeleton text :repeat="3" /></n-card>
                </n-grid-item>
              </template>
            </n-grid>
          </n-card>

          <n-card :bordered="false" title="最新动态" size="small" segmented>
            <n-list>
              <n-list-item v-for="item in recentArticles.slice(0, 5)" :key="'act-' + item.id">
                <template #prefix>
                  <n-avatar round size="small" :src="`https://api.dicebear.com/7.x/initials/svg?seed=${item.authorName || 'User'}`" />
                </template>
                <div class="activity-content">
                  <span class="username">{{ item.authorName || '管理员' }}</span>
                  <span class="action ml-2">发布了文章</span>
                  <span class="target ml-2 primary-color" @click="router.push(`/article/${item.id}`)" style="cursor: pointer">
                    {{ item.title }}
                  </span>
                </div>
                <template #suffix>
                  <span class="time-ago">{{ formatTimeAgo(item.createTime) }}</span>
                </template>
              </n-list-item>
            </n-list>
          </n-card>
        </n-space>
      </n-grid-item>

      <n-grid-item span="24 l:8">
        <n-space vertical size="large">
          <n-card :bordered="false" title="快捷导航" size="small" segmented>
            <n-grid cols="3" responsive="screen" :x-gap="8" :y-gap="16" class="quick-nav">
              <n-grid-item v-for="nav in quickNavs" :key="nav.label" class="text-center">
                <n-button text class="nav-btn" @click="nav.action">
                  <div class="flex flex-col items-center">
                    <n-icon size="24" :color="nav.color">
                      <component :is="nav.icon" />
                    </n-icon>
                    <div class="mt-2 text-xs">{{ nav.label }}</div>
                  </div>
                </n-button>
              </n-grid-item>
            </n-grid>
          </n-card>

          <n-card :bordered="false" title="分类概览" size="small" segmented>
            <div class="categories-wrapper">
              <n-grid :cols="2" :x-gap="12" :y-gap="12">
                <n-grid-item v-for="cat in categoryList.slice(0, 6)" :key="cat.id">
                  <div class="category-mini-item">
                    <span class="cat-name">{{ cat.name }}</span>
                    <span class="cat-count">{{ cat.articleCount }} 篇</span>
                  </div>
                </n-grid-item>
              </n-grid>
              <div v-if="categoryList.length === 0" class="text-gray-400 text-center py-4">暂无分类</div>
            </div>
          </n-card>

          <n-card :bordered="false" title="标签云" size="small" segmented>
            <div class="tags-wrapper flex flex-wrap gap-2">
              <n-tag 
                v-for="tag in tagList" 
                :key="tag.id" 
                :bordered="false" 
                size="small"
                :style="{ backgroundColor: 'rgba(0,0,0,0.05)' }"
              >
                {{ tag.name }}
              </n-tag>
              <span v-if="tagList.length === 0" class="text-gray-400 text-sm">暂无标签</span>
            </div>
          </n-card>
        </n-space>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h, defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
// 导入真实 API
import { getDashboardStats } from '@/api/dashboard'
import { getArticleList, getCategoryList, getTagList } from '@/api/article'
// 导入类型
import type { DashboardStats } from '@/api/dashboard'
import type { ArticleListVO } from '@/types/article'
import type { CategoryVO } from '@/types/category'
import type { TagVO } from '@/types/tag'
// 导入组件
import { 
  NCard, NAvatar, NGrid, NGridItem, NStatistic, NSpace, NButton, 
  NEllipsis, NList, NListItem, NIcon, NTag, NSkeleton, useMessage 
} from 'naive-ui'

// --- SVG 图标定义 (避免引入额外图标库，保持轻量) ---
const CreateIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z' })]) })
const ArticleIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M14 17H4v2h10v-2zm6-8H4v2h16V9zM4 15h16v-2H4v2zM4 5v2h16V5H4z' })]) })
const CategoryIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z' })]) })
const TagIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58c.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41c0-.55-.23-1.06-.59-1.42M5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7' })]) })
const SettingIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M19.14 12.94c.04-.3.06-.61.06-.94c0-.32-.02-.64-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.488.488 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.484.484 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6s3.6 1.62 3.6 3.6s-1.62 3.6-3.6 3.6z' })]) })
const HomeIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M10 20v-6h4v6h5v-8h3L12 3L2 12h3v8z' })]) })

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

// --- 状态数据 ---
const loading = ref(true)
const stats = ref<DashboardStats>({
  articleCount: 0,
  viewCount: 0,
  categoryCount: 0,
  tagCount: 0
})
const recentArticles = ref<ArticleListVO[]>([])
const categoryList = ref<CategoryVO[]>([])
const tagList = ref<TagVO[]>([])

// --- 初始化数据 ---
const initData = async () => {
  loading.value = true
  try {
    // 1. 获取核心统计
    const statsRes = await getDashboardStats()
    if (statsRes) stats.value = statsRes

    // 2. 获取近期文章 (复用文章列表接口，取第一页前6条)
    // 这里使用了现有的 getArticleList，不需要修改后端
    const articleRes = await getArticleList({ pageNum: 1, pageSize: 6 })
    if (articleRes && articleRes.records) {
      recentArticles.value = articleRes.records
    }

    // 3. 获取分类和标签 (用于侧边栏展示)
    const [catRes, tagRes] = await Promise.all([
      getCategoryList(),
      getTagList()
    ])
    categoryList.value = catRes || []
    tagList.value = tagRes || []

  } catch (error) {
    message.error('仪表盘数据加载失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// --- 快捷导航配置 ---
const quickNavs = [
  { label: '写文章', icon: CreateIcon, action: () => router.push('/admin/article/edit'), color: '#1890ff' },
  { label: '文章管理', icon: ArticleIcon, action: () => router.push('/admin/articles'), color: '#52c41a' },
  { label: '分类管理', icon: CategoryIcon, action: () => router.push('/admin/categories'), color: '#faad14' },
  { label: '标签管理', icon: TagIcon, action: () => router.push('/admin/tags'), color: '#eb2f96' },
  { label: '系统设置', icon: SettingIcon, action: () => router.push('/admin/settings'), color: '#722ed1' },
  { label: '前台首页', icon: HomeIcon, action: () => window.open('/', '_blank'), color: '#fa541c' },
]

// --- 工具函数 ---
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0] // 只显示日期部分
}

const formatTimeAgo = (dateStr: string | undefined) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = (now.getTime() - date.getTime()) / 1000 // 秒
  
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  return `${Math.floor(diff / 86400)}天前`
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.dashboard-container {
  /* 基础容器 */
}

.mr-2 { margin-right: 8px; }
.mt-4 { margin-top: 16px; }
.ml-2 { margin-left: 8px; }
.primary-color { color: var(--n-primary-color); }

/* 头部区域 */
.header-section {
  padding: 16px 24px;
  background: #fff;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.welcome-text {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.greeting {
  font-size: 20px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 8px;
}

.role-info {
  color: #6b7280;
  font-size: 14px;
}

.role-tag {
  margin-right: 12px;
  background: #eef2ff;
  color: #4f46e5;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.header-stats {
  min-width: 300px;
  text-align: right;
}

/* 覆盖 Naive UI 统计组件样式 */
:deep(.n-statistic .n-statistic__label) {
  color: #6b7280;
  font-size: 14px;
}
:deep(.n-statistic .n-statistic__value) {
  font-size: 24px;
  font-weight: 500;
}

/* 项目卡片 */
.project-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.06);
  border-color: var(--n-primary-color);
}

.project-header {
  display: flex;
  align-items: center;
}

.project-title {
  font-weight: 600;
  font-size: 15px;
  margin-left: 8px;
}

.project-desc {
  color: #6b7280;
  height: 40px; /* 固定高度确保对齐 */
  font-size: 13px;
  margin-top: 8px;
  line-height: 1.5;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #9ca3af;
  font-size: 12px;
  border-top: 1px solid #f3f4f6;
  padding-top: 12px;
}

/* 动态列表 */
.activity-content {
  font-size: 13px;
  line-height: 1.5;
}
.username { font-weight: 600; color: #374151; }
.action { color: #6b7280; }
.time-ago { color: #9ca3af; font-size: 12px; }

/* 快捷导航 */
.nav-btn {
  width: 100%;
  padding: 16px 0;
  border-radius: 8px;
  transition: background-color 0.2s;
}
.nav-btn:hover {
  background-color: #f9fafb;
}

/* 分类概览 */
.category-mini-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f9fafb;
  border-radius: 6px;
  font-size: 13px;
}
.cat-name { color: #374151; font-weight: 500; }
.cat-count { color: #9ca3af; }

/* 响应式适配 */
@media screen and (max-width: 900px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-stats {
    margin-top: 24px;
    width: 100%;
    text-align: left;
  }
  
  :deep(.header-stats .n-grid) {
    text-align: left;
  }
}
</style>