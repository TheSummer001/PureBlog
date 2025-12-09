<template>
  <div class="tags-management">
    <div class="header">
      <h1>标签管理</h1>
      <n-button type="primary" @click="showCreateModal = true">新增标签</n-button>
    </div>
    
    <n-data-table
      :columns="columns"
      :data="tagList"
      :loading="loading"
      :pagination="pagination"
      :bordered="false"
      remote
      @update:page="handlePageChange"
    />
    
    <!-- 新增/编辑标签弹窗 -->
    <n-modal v-model:show="showCreateModal" preset="card" :title="editingTag ? '编辑标签' : '新增标签'" style="width: 500px;">
      <n-form :model="tagForm" :rules="tagRules" ref="tagFormRef">
        <n-form-item label="标签名称" path="name">
          <n-input v-model:value="tagForm.name" placeholder="请输入标签名称" />
        </n-form-item>
        <n-form-item label="标签颜色" path="color">
          <n-color-picker v-model:value="tagForm.color" :show-alpha="false" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button @click="showCreateModal = false" style="margin-right: 10px;">取消</n-button>
        <n-button type="primary" @click="saveTag" :loading="saving">保存</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NButton, NColorPicker, NDataTable, NModal, NForm, NFormItem, NInput, useMessage } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import type { TagVO } from '@/types/tag'

interface Tag {
  id: number
  name: string
  color: string
  articleCount: number
  createdAt: string
}

// 模拟数据
const tagList = ref<Tag[]>([
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

const loading = ref(false)
const saving = ref(false)
const showCreateModal = ref(false)
const editingTag = ref<Tag | null>(null)
const tagFormRef = ref<FormInst | null>(null)

const tagForm = ref({
  name: '',
  color: '#18a058'
})

const tagRules: FormRules = {
  name: [
    {
      required: true,
      message: '请输入标签名称',
      trigger: ['blur']
    }
  ]
}

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
    render(row) {
      return [
        h(
          NButton,
          {
            size: 'small',
            style: {
              marginRight: '8px'
            },
            onClick: () => editTag(row)
          },
          { default: () => '编辑' }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            onClick: () => deleteTag(row.id)
          },
          { default: () => '删除' }
        )
      ]
    }
  }
]

const pagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 2,
  showSizePicker: true,
  pageSizes: [10, 20, 30],
  onChange: (page: number) => {
    pagination.value.page = page
    fetchTagList()
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.value.pageSize = pageSize
    pagination.value.page = 1
    fetchTagList()
  }
})

const message = useMessage()

// 获取标签列表
const fetchTagList = () => {
  // 模拟API调用
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 300)
}

// 编辑标签
const editTag = (tag: Tag) => {
  editingTag.value = tag
  tagForm.value = {
    name: tag.name,
    color: tag.color
  }
  showCreateModal.value = true
}

// 删除标签
const deleteTag = (id: number) => {
  const modal = window.$dialog?.warning({
    title: '确认删除',
    content: '确定要删除这个标签吗？此操作不可恢复。',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      // 模拟API调用
      tagList.value = tagList.value.filter(tag => tag.id !== id)
      message.success('删除成功')
    }
  })
}

// 保存标签
const saveTag = () => {
  tagFormRef.value?.validate(async (errors) => {
    if (errors) return
    
    saving.value = true
    try {
      if (editingTag.value) {
        // 编辑模式
        const index = tagList.value.findIndex(tag => tag.id === editingTag.value?.id)
        if (index >= 0) {
          tagList.value[index] = {
            id: editingTag.value.id,
            name: tagForm.value.name,
            color: tagForm.value.color,
            articleCount: tagList.value[index]?.articleCount || 0,
            createdAt: tagList.value[index]?.createdAt || ''
          } as Tag
        }
        message.success('标签更新成功')
      } else {
        // 新增模式
        // 使用负数作为临时ID，避免与真实ID冲突
        let tempId = -(tagList.value.length + 1);
        // 确保临时ID唯一
        while (tagList.value.some(tag => tag.id === tempId)) {
          tempId--;
        }
        
        const newTag: Tag = {
          id: tempId,
          name: tagForm.value.name,
          color: tagForm.value.color,
          articleCount: 0,
          createdAt: new Date().toISOString().split('T')[0] || ''
        }
        tagList.value.push(newTag)
        message.success('标签添加成功')
      }
      
      showCreateModal.value = false
      resetForm()
    } catch (error) {
      message.error(editingTag.value ? '标签更新失败' : '标签添加失败')
      console.error(error)
    } finally {
      saving.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  tagForm.value = {
    name: '',
    color: '#18a058'
  }
  editingTag.value = null
}

// 页面变更处理
const handlePageChange = (page: number) => {
  pagination.value.page = page
  fetchTagList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchTagList()
})
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