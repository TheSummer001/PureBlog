<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <n-grid cols="1 s:2 m:4" responsive="screen" :x-gap="16" :y-gap="16">
      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-info">
              <n-statistic tabular-nums>
                <n-number-animation :from="0" :to="stats.articleCount" />
                <template #prefix>
                  <div class="stat-label">文章总数</div>
                </template>
              </n-statistic>
            </div>
            <div class="stat-icon bg-primary/10 text-primary">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
              </n-icon>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-info">
              <n-statistic tabular-nums>
                <n-number-animation :from="0" :to="stats.viewCount" show-separator />
                <template #prefix>
                  <div class="stat-label">总阅读量</div>
                </template>
              </n-statistic>
            </div>
            <div class="stat-icon bg-primary/10 text-primary">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5s5 2.24 5 5s-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3s3-1.34 3-3s-1.34-3-3-3z"/></svg>
              </n-icon>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-info">
              <n-statistic tabular-nums>
                <n-number-animation :from="0" :to="stats.categoryCount" />
                <template #prefix>
                  <div class="stat-label">分类数</div>
                </template>
              </n-statistic>
            </div>
            <div class="stat-icon bg-primary/10 text-primary">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
              </n-icon>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-info">
              <n-statistic tabular-nums>
                <n-number-animation :from="0" :to="stats.tagCount" />
                <template #prefix>
                  <div class="stat-label">标签数</div>
                </template>
              </n-statistic>
            </div>
            <div class="stat-icon bg-primary/10 text-primary">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58c.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41c0-.55-.23-1.06-.59-1.42M5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7"/></svg>
              </n-icon>
            </div>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <!-- 快捷开始 -->
    <n-grid cols="1 s:2" responsive="screen" :x-gap="16" :y-gap="16" class="mt-4">
      <n-grid-item>
        <n-card 
          class="quick-action-card" 
          :bordered="false" 
          content-style="padding: 0;"
          @click="handleWriteArticle"
        >
          <div class="quick-action-content">
            <div class="quick-action-icon bg-gradient-to-r from-blue-400 to-indigo-500">
              <n-icon size="32" color="white">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
              </n-icon>
            </div>
            <div class="quick-action-text">
              <h3 class="quick-action-title">写文章</h3>
              <p class="quick-action-desc">创建新的博客文章</p>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card 
          class="quick-action-card" 
          :bordered="false" 
          content-style="padding: 0;"
          @click="handleSettings"
        >
          <div class="quick-action-content">
            <div class="quick-action-icon bg-gradient-to-r from-purple-400 to-pink-500">
              <n-icon size="32" color="white">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M12 15.5A3.5 3.5 0 0 1 8.5 12A3.5 3.5 0 0 1 12 8.5a3.5 3.5 0 0 1 3.5 3.5a3.5 3.5 0 0 1-3.5 3.5m7.43-2.53c.04-.32.07-.64.07-.97c0-.33-.03-.66-.07-1l2.11-1.63c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.31-.61-.22l-2.49 1c-.52-.39-1.06-.73-1.69-.98l-.37-2.65A.506.506 0 0 0 14 2h-4c-.25 0-.46.18-.5.42l-.37 2.65c-.63.25-1.17.59-1.69.98l-2.49-1c-.22-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64L4.57 11c-.04.34-.07.67-.07 1c0 .33.03.65.07.97l-2.11 1.66c-.19.15-.25.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1.01c.52.4 1.06.74 1.69.99l.37 2.65c.04.24.25.42.5.42h4c.25 0 .46-.18.5-.42l.37-2.65c.63-.26 1.17-.59 1.69-.99l2.49 1.01c.22.08.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.66Z"/></svg>
              </n-icon>
            </div>
            <div class="quick-action-text">
              <h3 class="quick-action-title">系统设置</h3>
              <p class="quick-action-desc">配置博客系统参数</p>
            </div>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <!-- 近期文章表现 -->
    <n-grid cols="1" responsive="screen" :x-gap="16" :y-gap="16" class="mt-4">
      <n-grid-item>
        <n-card title="近期文章表现" :bordered="false" class="recent-card">
          <n-data-table
            :columns="columns"
            :data="recentArticles"
            :bordered="false"
            size="small"
          />
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, h } from 'vue'
import { useRouter } from 'vue-router'
import { 
  NGrid, NGridItem, NCard, NStatistic, NNumberAnimation, NIcon, 
  NDataTable, useMessage 
} from 'naive-ui'
import { getDashboardStats } from '@/api/dashboard'
import type { DashboardStats } from '@/api/dashboard'

const router = useRouter()
const message = useMessage()

// 统计数据
const stats = reactive<DashboardStats>({
  articleCount: 0,
  viewCount: 0,
  categoryCount: 0,
  tagCount: 0
})

// 近期文章数据
const recentArticles = ref([
  {
    id: '1',
    title: 'Vue 3 Composition API 实战指南',
    views: 1245,
    status: 1
  },
  {
    id: '2',
    title: 'TypeScript 高级类型详解',
    views: 987,
    status: 1
  },
  {
    id: '3',
    title: '构建现代化的博客系统',
    views: 756,
    status: 0
  },
  {
    id: '4',
    title: '响应式设计最佳实践',
    views: 632,
    status: 1
  },
  {
    id: '5',
    title: '性能优化技巧分享',
    views: 543,
    status: 1
  }
])

// 表格列配置
const columns = [
  {
    title: '文章标题',
    key: 'title',
    ellipsis: true
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row: any) {
      return row.status === 1 
        ? h('span', { class: 'status-badge published' }, '已发布')
        : h('span', { class: 'status-badge draft' }, '草稿')
    }
  },
  {
    title: '阅读量',
    key: 'views',
    width: 120,
    render(row: any) {
      return h('span', { class: 'font-medium' }, row.views.toLocaleString())
    }
  }
]

// 初始化数据
const initData = async () => {
  try {
    const statsRes = await getDashboardStats()
    Object.assign(stats, statsRes)
  } catch (error) {
    message.error('获取仪表盘数据失败')
    console.error(error)
  }
}

// 处理写文章
const handleWriteArticle = () => {
  router.push('/admin/article/edit')
}

// 处理系统设置
const handleSettings = () => {
  router.push('/admin/settings')
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
}

.stat-card {
  transition: all 0.3s ease;
  cursor: default;
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.n-statistic {
  font-weight: 700;
  font-size: 24px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-action-card {
  transition: all 0.3s ease;
  cursor: pointer;
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

.quick-action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);
}

.quick-action-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.quick-action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.quick-action-text {
  flex: 1;
}

.quick-action-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #1f2937;
}

.quick-action-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.recent-card {
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

:deep(.recent-card .n-card-header) {
  padding: 20px 20px 12px 20px;
}

:deep(.recent-card .n-card__content) {
  padding: 0 20px 20px 20px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.published {
  background-color: #dcfce7;
  color: #166534;
}

.status-badge.draft {
  background-color: #fef3c7;
  color: #92400e;
}

.font-medium {
  font-weight: 500;
}
</style>