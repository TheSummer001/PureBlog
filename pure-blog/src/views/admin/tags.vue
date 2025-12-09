<template>
  <div class="tags-management">
    <div class="header">
      <h1>标签管理</h1>
      <n-button type="primary">新增标签</n-button>
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

interface Tag {
  id: number
  name: string
  color: string
  articleCount: number
  createdAt: string
}

const data = ref<Tag[]>([
  {
    id: 1,
    name: 'Vue',
    color: '#42b983',
    articleCount: 8,
    createdAt: '2025-01-01'
  },
  {
    id: 2,
    name: 'TypeScript',
    color: '#3178c6',
    articleCount: 5,
    createdAt: '2025-01-02'
  }
])

const columns: DataTableColumns<Tag> = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '标签名称',
    key: 'name',
    render(row) {
      return h(
        'span',
        {
          style: {
            backgroundColor: row.color,
            color: '#fff',
            padding: '2px 8px',
            borderRadius: '4px',
            fontSize: '12px'
          }
        },
        { default: () => row.name }
      )
    }
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
.tags-management {
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