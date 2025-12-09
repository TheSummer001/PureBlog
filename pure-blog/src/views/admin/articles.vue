<template>
  <div class="articles-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="title">文章管理</h2>
        <span class="subtitle">管理博客的所有文章内容、分类及发布状态</span>
      </div>
      <div class="header-action">
        <n-button type="primary" size="medium" @click="handleCreate">
          <template #icon>
            <n-icon><AddIcon /></n-icon>
          </template>
          新建文章
        </n-button>
      </div>
    </div>

    <n-card :bordered="false" class="filter-card" size="small">
      <n-form inline :model="searchForm" label-placement="left" label-width="auto" class="search-form">
        <n-grid :x-gap="24" :y-gap="16" cols="1 s:2 m:3 l:4" responsive="screen">
          <n-grid-item>
            <n-form-item label="文章标题" path="title">
              <n-input v-model:value="searchForm.title" placeholder="输入标题关键词" clearable />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="所属分类" path="categoryId">
              <n-select 
                v-model:value="searchForm.categoryId" 
                :options="categoryOptions" 
                placeholder="全部分类" 
                clearable
                filterable
              />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="发布状态" path="status">
              <n-select 
                v-model:value="searchForm.status" 
                :options="statusOptions" 
                placeholder="全部状态" 
                clearable
              />
            </n-form-item>
          </n-grid-item>
          <n-grid-item class="action-col">
            <n-space>
              <n-button type="primary" secondary @click="handleSearch">
                <template #icon><n-icon><SearchIcon /></n-icon></template>
                查询
              </n-button>
              <n-button @click="handleReset">
                <template #icon><n-icon><RefreshIcon /></n-icon></template>
                重置
              </n-button>
            </n-space>
          </n-grid-item>
        </n-grid>
      </n-form>
    </n-card>

    <n-card :bordered="false" class="table-card" content-style="padding: 0;">
      <n-data-table
        :columns="columns"
        :data="articleList"
        :loading="loading"
        :pagination="pagination"
        :bordered="false"
        remote
        :row-key="(row) => row.id"
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
        size="large"
      />
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import { 
  NButton, NCard, NForm, NFormItem, NInput, NSelect, NDataTable, 
  NGrid, NGridItem, NSpace, NIcon, NTag, NPopconfirm, useMessage, NTooltip
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getArticleList, deleteArticle, getCategoryList } from '@/api/article'
import type { ArticleListVO, ArticleQueryDTO } from '@/types/article'

// --- 图标定义 (内联 SVG 以保持无额外依赖) ---
const AddIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M19 12.998h-6v6h-2v-6H5v-2h6v-6h2v6h6z' })]) })
const SearchIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5A6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z' })]) })
const RefreshIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M17.65 6.35A7.958 7.958 0 0 0 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08A5.99 5.99 0 0 1 12 18c-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z' })]) })
const EditIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z' })]) })
const DeleteIcon = defineComponent({ render: () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', width: '1em', height: '1em' }, [h('path', { fill: 'currentColor', d: 'M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z' })]) })

const router = useRouter()
const message = useMessage()

// --- 状态与数据 ---
const loading = ref(false)
const articleList = ref<ArticleListVO[]>([])
const categoryOptions = ref<{ label: string; value: number }[]>([])

// 搜索表单
const searchForm = reactive<ArticleQueryDTO>({
  title: undefined,
  categoryId: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页配置 (修复关键：itemCount?: number 且使用 ?? 0)
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 30, 50],
  prefix: ({ itemCount }: { itemCount?: number }) => `共 ${itemCount ?? 0} 篇文章`
})

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 }
]

// --- 列定义 ---
const columns: DataTableColumns<ArticleListVO> = [
  { 
    title: '文章标题', 
    key: 'title', 
    ellipsis: { tooltip: true },
    minWidth: 200,
    render(row) {
      return h('span', { class: 'font-medium text-gray-700' }, row.title)
    }
  },
  { 
    title: '分类', 
    key: 'categoryName', 
    width: 140,
    render(row) {
      return row.categoryName 
        ? h(NTag, { type: 'info', bordered: false, size: 'small', class: 'bg-indigo-50 text-indigo-600' }, { default: () => row.categoryName })
        : h('span', { class: 'text-gray-400' }, '未分类')
    }
  },
  {
    title: '标签',
    key: 'tags',
    width: 200,
    render(row) {
      if (!row.tags || row.tags.length === 0) return h('span', { class: 'text-gray-300' }, '-')
      // 只显示前2个标签，其他的显示+N
      const displayTags = row.tags.slice(0, 2)
      const moreCount = row.tags.length - 2
      
      const tags = displayTags.map(tag => 
        h(NTag, { 
          size: 'small',
          bordered: false,
          style: { marginRight: '6px' }
        }, { default: () => tag.name })
      )
      
      if (moreCount > 0) {
        tags.push(h(NTag, { size: 'small', bordered: false, type: 'default' }, { default: () => `+${moreCount}` }))
      }
      return tags
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    render(row) {
      const isPublished = row.status === 1
      // 使用小圆点状态样式
      return h('div', { class: 'flex items-center' }, [
        h('span', { 
          class: `inline-block w-2 h-2 rounded-full mr-2 ${isPublished ? 'bg-green-500 shadow-sm shadow-green-200' : 'bg-yellow-500 shadow-sm shadow-yellow-200'}` 
        }),
        h('span', { class: isPublished ? 'text-green-600' : 'text-yellow-600' }, isPublished ? '已发布' : '草稿')
      ])
    }
  },
  { 
    title: '发布时间', 
    key: 'publishTime', 
    width: 180,
    render(row) {
      return h('span', { class: 'text-gray-500 font-mono text-xs' }, row.publishTime || row.createTime)
    }
  },
  { 
    title: '数据', 
    key: 'views', 
    width: 100,
    render(row) {
      return h(NTooltip, { trigger: 'hover' }, {
        trigger: () => h('span', { class: 'text-gray-500 cursor-help' }, `👁 ${row.views}`),
        default: () => '文章阅读量'
      })
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    fixed: 'right',
    render(row) {
      return h(NSpace, { align: 'center', size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'tiny',
            quaternary: true,
            type: 'primary',
            onClick: () => handleEdit(row.id)
          }, { icon: () => h(NIcon, null, { default: () => h(EditIcon) }) }),
          
          h(NPopconfirm, {
            onPositiveClick: () => handleDelete(row.id),
            positiveText: '确认删除',
            negativeText: '取消'
          }, {
            trigger: () => h(NButton, {
              size: 'tiny',
              quaternary: true,
              type: 'error'
            }, { icon: () => h(NIcon, null, { default: () => h(DeleteIcon) }) }),
            default: () => '确定要删除这篇文章吗？此操作不可恢复。'
          })
        ]
      })
    }
  }
]

// --- 方法 ---

const fetchArticleList = async () => {
  loading.value = true
  try {
    const response = await getArticleList(searchForm)
    articleList.value = response.records
    pagination.page = response.current
    pagination.pageSize = response.size
    pagination.itemCount = response.total
  } catch (error) {
    message.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const fetchCategoryList = async () => {
  try {
    const response = await getCategoryList()
    categoryOptions.value = response.map(c => ({ label: c.name, value: Number(c.id) }))
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  searchForm.pageNum = 1
  fetchArticleList()
}

const handleReset = () => {
  searchForm.title = undefined
  searchForm.categoryId = undefined
  searchForm.status = undefined
  handleSearch()
}

const handlePageChange = (page: number) => {
  searchForm.pageNum = page
  fetchArticleList()
}

const handlePageSizeChange = (pageSize: number) => {
  searchForm.pageSize = pageSize
  searchForm.pageNum = 1
  fetchArticleList()
}

const handleCreate = () => {
  router.push('/admin/article/edit')
}

const handleEdit = (id: string | number) => {
  router.push(`/admin/article/edit/${id}`)
}

const handleDelete = async (id: string | number) => {
  try {
    // 强制转换为 any 避免 TS 对 id 类型报错，后端使用 String 接收不会有问题
    await deleteArticle(id as any)
    message.success('删除成功')
    fetchArticleList()
  } catch (error) {
    // request.ts 已处理错误提示
  }
}

// --- 生命周期 ---
onMounted(() => {
  fetchArticleList()
  fetchCategoryList()
})
</script>

<style scoped>
.articles-container {
  /* 页面容器不需要内边距，靠内部元素撑开，显得更现代 */
  min-height: 100%;
}

/* 头部样式 */
.page-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-title .title {
  font-size: 20px;
  font-weight: 500;
  color: #1f2937;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.header-title .subtitle {
  font-size: 13px;
  color: #6b7280;
}

/* 筛选卡片 */
.filter-card {
  margin: 0 24px 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.action-col {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

/* 表格卡片 */
.table-card {
  margin: 0 24px 24px;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 表格样式微调 */
:deep(.n-data-table .n-data-table-th) {
  background-color: #fafafc;
  font-weight: 500;
  color: #4b5563;
}

:deep(.n-data-table .n-data-table-td) {
  padding: 16px;
}

/* 字体工具类模拟 (如果项目没引入 UnoCSS) */
.font-medium { font-weight: 500; }
.font-mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; }
.text-xs { font-size: 12px; }
.text-gray-700 { color: #374151; }
.text-gray-500 { color: #6b7280; }
.text-gray-400 { color: #9ca3af; }
.text-gray-300 { color: #d1d5db; }
.text-green-600 { color: #16a34a; }
.text-yellow-600 { color: #ca8a04; }
.bg-green-500 { background-color: #22c55e; }
.bg-yellow-500 { background-color: #eab308; }
.shadow-sm { box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); }
.cursor-help { cursor: help; }
.flex { display: flex; }
.items-center { align-items: center; }
.mr-2 { margin-right: 8px; }
.inline-block { display: inline-block; }
.w-2 { width: 0.5rem; }
.h-2 { height: 0.5rem; }
.rounded-full { border-radius: 9999px; }
.bg-indigo-50 { background-color: #eef2ff; }
.text-indigo-600 { color: #4f46e5; }
</style>