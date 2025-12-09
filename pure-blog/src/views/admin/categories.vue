<template>
  <div class="categories-management">
    <div class="header">
      <h1>分类管理</h1>
      <n-button type="primary">新增分类</n-button>
    </div>
    <n-data-table
      :columns="columns"
      :data="data"
      :pagination="pagination"
      :bordered="false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { NButton } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'

interface Category {
  id: number
  name: string
  articleCount: number
  createdAt: string
}

const data = ref<Category[]>([
  {
    id: 1,
    name: '前端开发',
    articleCount: 10,
    createdAt: '2025-01-01'
  },
  {
    id: 2,
    name: '后端开发',
    articleCount: 5,
    createdAt: '2025-01-02'
  }
])

const columns: DataTableColumns<Category> = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '分类名称',
    key: 'name'
  },
  {
    title: '文章数量',
    key: 'articleCount',
    width: 120
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 120
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render() {
      return [
        h(
          NButton,
          {
            size: 'small',
            style: {
              marginRight: '8px'
            }
          },
          { default: () => '编辑' }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error'
          },
          { default: () => '删除' }
        )
      ]
    }
  }
]

const pagination = {
  pageSize: 10
}
</script>

<style scoped>
.categories-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h1 {
  margin: 0;
}
</style>