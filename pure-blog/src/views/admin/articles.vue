<template>
  <div class="articles-management">
    <div class="header">
      <h1>文章管理</h1>
      <n-button type="primary" @click="handleCreate">新建文章</n-button>
    </div>
    
    <n-card class="search-card">
      <n-form inline :model="searchForm" label-placement="left">
        <n-form-item label="标题">
          <n-input v-model:value="searchForm.title" placeholder="请输入标题" clearable />
        </n-form-item>
        <n-form-item label="分类">
          <n-select 
            v-model:value="searchForm.categoryId" 
            :options="categoryOptions" 
            placeholder="请选择分类" 
            clearable
            filterable
          />
        </n-form-item>
        <n-form-item label="状态">
          <n-select 
            v-model:value="searchForm.status" 
            :options="statusOptions" 
            placeholder="请选择状态" 
            clearable
          />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button @click="handleReset" style="margin-left: 10px;">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>
    
    <n-data-table
      :columns="columns"
      :data="articleList"
      :loading="loading"
      :pagination="pagination"
      :bordered="false"
      remote
      @update:page="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getArticleList, deleteArticle } from '@/api/article'
import { getCategoryList } from '@/api/article'
import type { ArticleListVO, ArticleQueryDTO } from '@/types/article'
import type { CategoryVO } from '@/types/category'
import type { Page } from '@/types/page' // ✅ 修复：导入 Page 类型

const router = useRouter()
const message = useMessage()

// 搜索表单
const searchForm = ref<ArticleQueryDTO>({
  title: undefined,
  categoryId: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 加载状态
const loading = ref(false)

// 文章列表数据
const articleList = ref<ArticleListVO[]>([])

// 分页配置
const pagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0, // ✅ 修复：初始化 itemCount，解决 TS 报错
  showSizePicker: true,
  pageSizes: [10, 20, 30],
  onChange: (page: number) => {
    searchForm.value.pageNum = page
    handleSearch()
  },
  onUpdatePageSize: (pageSize: number) => {
    searchForm.value.pageSize = pageSize
    searchForm.value.pageNum = 1
    handleSearch()
  }
})

// 分类选项
const categoryOptions = ref<{ label: string; value: number }[]>([])

// 状态选项
const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 }
]

// 表格列配置
const columns: DataTableColumns<ArticleListVO> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '标题', key: 'title', ellipsis: true },
  { title: '分类', key: 'categoryName', width: 120 },
  {
    title: '标签',
    key: 'tags',
    width: 150,
    render(row) {
      return row.tags.map(tag => 
        h(NTag, { 
          type: 'success',
          size: 'small',
          style: { marginRight: '4px' }
        }, { default: () => tag.name })
      )
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row) {
      return h(NTag, { 
        type: row.status === 1 ? 'success' : 'warning'
      }, { 
        default: () => row.status === 1 ? '已发布' : '草稿' 
      })
    }
  },
  { title: '发布时间', key: 'publishTime', width: 160 },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render(row) {
      return [
        h(NButton, {
            size: 'small',
            type: 'primary',
            style: { marginRight: '8px' },
            onClick: () => handleEdit(row.id)
          }, { default: () => '编辑' }),
        h(NButton, {
            size: 'small',
            type: 'error',
            onClick: () => handleDelete(row.id)
          }, { default: () => '删除' })
      ]
    }
  }
]

// 获取文章列表
const fetchArticleList = async () => {
  loading.value = true
  try {
    // ✅ 修复：这里 await 的结果已经是 Page<ArticleListVO> 了，不需要再 .data
    const response = await getArticleList(searchForm.value)
    articleList.value = response.records
    pagination.value.page = response.current
    pagination.value.pageSize = response.size
    pagination.value.itemCount = response.total
  } catch (error) {
    message.error('获取文章列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategoryList = async () => {
  try {
    // ✅ 修复：这里 await 的结果已经是 CategoryVO[]
    const response = await getCategoryList()
    categoryOptions.value = response.map(category => ({
      label: category.name,
      value: category.id
    }))
  } catch (error) {
    message.error('获取分类列表失败')
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  fetchArticleList()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    title: undefined,
    categoryId: undefined,
    status: undefined,
    pageNum: 1,
    pageSize: 10
  }
  fetchArticleList()
}

// 分页变化
const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  fetchArticleList()
}

// 新建文章
const handleCreate = () => {
  router.push('/admin/article/edit')
}

// 编辑文章
const handleEdit = (id: number) => {
  router.push(`/admin/article/edit/${id}`)
}

// 删除文章
const handleDelete = async (id: number) => {
  const confirm = await new Promise(resolve => {
    // ✅ 修复：使用 window.$dialog (类型已在步骤1中定义)
    window.$dialog?.warning({
      title: '确认删除',
      content: '确定要删除这篇文章吗？此操作不可恢复。',
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => resolve(true),
      onNegativeClick: () => resolve(false)
    })
  })
  
  if (confirm) {
    try {
      await deleteArticle(id)
      message.success('删除成功')
      fetchArticleList() // 重新加载列表
    } catch (error) {
      // 错误已由 request.ts 处理，这里只需 catch 防止控制台报 Uncaught Error
    }
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchArticleList()
  fetchCategoryList()
})
</script>

<style scoped>
.articles-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

h1 {
  margin: 0;
}
</style>