<template>
  <div class="categories-tags-management">
    <div class="header">
      <h1>分类与标签</h1>
    </div>
    
    <n-grid cols="1 xl:2" responsive="screen" :x-gap="20" :y-gap="20">
      <!-- 分类管理 -->
      <n-grid-item>
        <n-card title="分类管理" :bordered="false" class="section-card">
          <template #header-extra>
            <n-button type="primary" size="small" @click="handleCreateCategory">
              <template #icon>
                <n-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10s10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"/></svg>
                </n-icon>
              </template>
              新增分类
            </n-button>
          </template>
          
          <div class="category-list">
            <div 
              v-for="category in categoryList" 
              :key="category.id" 
              class="category-item"
            >
              <div class="category-icon">
                <n-icon size="20" color="#4f46e5">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="currentColor" d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
                </n-icon>
              </div>
              <div class="category-info">
                <div class="category-name">{{ category.name }}</div>
                <div class="category-meta">
                  <span class="category-count">{{ category.articleCount || 0 }} 篇文章</span>
                  <span class="category-status" :class="{ disabled: category.status === 0 }">
                    {{ category.status === 1 ? '启用' : '禁用' }}
                  </span>
                </div>
              </div>
              <div class="category-actions">
                <n-button 
                  size="small" 
                  quaternary 
                  type="primary" 
                  @click="handleEditCategory(category)"
                >
                  编辑
                </n-button>
              </div>
            </div>
            
            <div v-if="categoryList.length === 0" class="empty-state">
              暂无分类数据
            </div>
          </div>
        </n-card>
      </n-grid-item>
      
      <!-- 标签管理 -->
      <n-grid-item>
        <n-card title="标签管理" :bordered="false" class="section-card">
          <template #header-extra>
            <n-button type="primary" size="small" @click="handleCreateTag">
              <template #icon>
                <n-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10s10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"/></svg>
                </n-icon>
              </template>
              新增标签
            </n-button>
          </template>
          
          <div class="tag-cloud">
            <n-tag
              v-for="tag in tagList"
              :key="tag.id"
              :color="{ 
                color: tag.color || getRandomColor(),
                textColor: '#fff',
                borderColor: 'transparent'
              }"
              round
              size="large"
              class="tag-item"
              closable
              @close="handleDeleteTag(tag.id)"
            >
              {{ tag.name }}
              <template #avatar>
                <n-avatar 
                  :style="{ 
                    backgroundColor: tag.color || getRandomColor(),
                    color: '#fff'
                  }" 
                  size="small"
                >
                  {{ tag.articleCount || 0 }}
                </n-avatar>
              </template>
            </n-tag>
            
            <div v-if="tagList.length === 0" class="empty-state">
              暂无标签数据
            </div>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>
    
    <!-- 分类编辑弹窗 -->
    <n-modal 
      v-model:show="showCategoryModal" 
      preset="card" 
      :title="editingCategory ? '编辑分类' : '新增分类'" 
      style="width: 500px;"
      :bordered="false"
    >
      <n-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef">
        <n-form-item label="分类名称" path="name">
          <n-input v-model:value="categoryForm.name" placeholder="请输入分类名称" />
        </n-form-item>
        <n-form-item label="分类描述" path="description">
          <n-input v-model:value="categoryForm.description" placeholder="请输入分类描述" type="textarea" />
        </n-form-item>
        <n-form-item label="排序" path="sort">
          <n-input-number v-model:value="categoryForm.sort" placeholder="请输入排序值" />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="categoryForm.status">
            <n-radio :value="1">启用</n-radio>
            <n-radio :value="0">禁用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button @click="showCategoryModal = false" style="margin-right: 10px;">取消</n-button>
        <n-button type="primary" @click="saveCategory" :loading="savingCategory">保存</n-button>
      </template>
    </n-modal>
    
    <!-- 标签编辑弹窗 -->
    <n-modal 
      v-model:show="showTagModal" 
      preset="card" 
      :title="editingTag ? '编辑标签' : '新增标签'" 
      style="width: 500px;"
      :bordered="false"
    >
      <n-form :model="tagForm" :rules="tagRules" ref="tagFormRef">
        <n-form-item label="标签名称" path="name">
          <n-input v-model:value="tagForm.name" placeholder="请输入标签名称" />
        </n-form-item>
        <n-form-item label="标签颜色" path="color">
          <n-color-picker v-model:value="tagForm.color" :show-alpha="false" />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="tagForm.status">
            <n-radio :value="1">启用</n-radio>
            <n-radio :value="0">禁用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button @click="showTagModal = false" style="margin-right: 10px;">取消</n-button>
        <n-button type="primary" @click="saveTag" :loading="savingTag">保存</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  NGrid, NGridItem, NCard, NButton, NIcon, NModal, NForm, NFormItem, 
  NInput, NInputNumber, NRadioGroup, NRadio, NTag, NAvatar, useMessage 
} from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { getTagList, createTag, updateTag, deleteTag } from '@/api/tag'
import type { CategoryVO } from '@/types/category'
import type { TagVO } from '@/types/tag'

const message = useMessage()

// 分类相关
const categoryList = ref<CategoryVO[]>([])
const loadingCategories = ref(false)
const showCategoryModal = ref(false)
const savingCategory = ref(false)
const editingCategory = ref<CategoryVO | null>(null)
const categoryFormRef = ref<FormInst | null>(null)

const categoryForm = ref({
  name: '',
  description: '',
  sort: 0,
  status: 1
})

const categoryRules: FormRules = {
  name: [
    {
      required: true,
      message: '请输入分类名称',
      trigger: ['blur']
    }
  ],
  sort: [
    {
      required: true,
      message: '请输入排序值',
      trigger: ['blur']
    }
  ],
  status: [
    {
      required: true,
      message: '请选择状态',
      trigger: ['blur']
    }
  ]
}

// 标签相关
const tagList = ref<TagVO[]>([])
const loadingTags = ref(false)
const showTagModal = ref(false)
const savingTag = ref(false)
const editingTag = ref<TagVO | null>(null)
const tagFormRef = ref<FormInst | null>(null)

const tagForm = ref({
  name: '',
  color: '#4f46e5',
  status: 1
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

// 随机颜色生成器
const getRandomColor = () => {
  const colors = [
    '#4f46e5', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', 
    '#0ea5e9', '#ec4899', '#14b8a6', '#f97316', '#64748b'
  ]
  return colors[Math.floor(Math.random() * colors.length)]
}

// 获取分类列表
const fetchCategoryList = async () => {
  loadingCategories.value = true
  try {
    const response = await getCategoryList()
    categoryList.value = response
  } catch (error) {
    message.error('获取分类列表失败')
    console.error(error)
  } finally {
    loadingCategories.value = false
  }
}

// 获取标签列表
const fetchTagList = async () => {
  loadingTags.value = true
  try {
    const response = await getTagList()
    tagList.value = response
  } catch (error) {
    message.error('获取标签列表失败')
    console.error(error)
  } finally {
    loadingTags.value = false
  }
}

// 新增分类
const handleCreateCategory = () => {
  editingCategory.value = null
  categoryForm.value = {
    name: '',
    description: '',
    sort: 0,
    status: 1
  }
  showCategoryModal.value = true
}

// 编辑分类
const handleEditCategory = (category: CategoryVO) => {
  editingCategory.value = category
  categoryForm.value = {
    name: category.name,
    description: category.description || '',
    sort: category.sort,
    status: category.status
  }
  showCategoryModal.value = true
}

// 保存分类
const saveCategory = () => {
  categoryFormRef.value?.validate(async (errors) => {
    if (errors) return
    
    savingCategory.value = true
    try {
      if (editingCategory.value) {
        // 编辑模式
        const numericId = typeof editingCategory.value.id === 'string' ? Number(editingCategory.value.id) : editingCategory.value.id
        await updateCategory(numericId, categoryForm.value)
        message.success('分类更新成功')
      } else {
        // 新增模式
        await createCategory(categoryForm.value)
        message.success('分类添加成功')
      }
      
      showCategoryModal.value = false
      fetchCategoryList()
    } catch (error) {
      message.error(editingCategory.value ? '分类更新失败' : '分类添加失败')
      console.error(error)
    } finally {
      savingCategory.value = false
    }
  })
}

// 删除分类确认
const deleteCategoryConfirm = (id: string | number) => {
  const category = categoryList.value.find(c => c.id === id)
  const categoryName = category ? category.name : '该分类'
  
  window.$dialog?.warning({
    title: '确认删除',
    content: `确定要删除分类"${categoryName}"吗？此操作不可恢复。`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => deleteCategoryHandler(id)
  })
}

// 删除分类
const deleteCategoryHandler = async (id: string | number) => {
  try {
    const numericId = typeof id === 'string' ? Number(id) : id
    await deleteCategory(numericId)
    message.success('删除成功')
    fetchCategoryList()
  } catch (error) {
    message.error('删除失败')
    console.error(error)
  }
}

// 新增标签
const handleCreateTag = () => {
  editingTag.value = null
  tagForm.value = {
    name: '',
    color: '#4f46e5',
    status: 1
  }
  showTagModal.value = true
}

// 编辑标签
const handleEditTag = (tag: TagVO) => {
  editingTag.value = tag
  tagForm.value = {
    name: tag.name,
    color: tag.color || '#4f46e5',
    status: tag.status
  }
  showTagModal.value = true
}

// 保存标签
const saveTag = () => {
  tagFormRef.value?.validate(async (errors) => {
    if (errors) return
    
    savingTag.value = true
    try {
      if (editingTag.value) {
        // 编辑模式
        const numericId = typeof editingTag.value.id === 'string' ? Number(editingTag.value.id) : editingTag.value.id
        await updateTag(numericId, tagForm.value)
        message.success('标签更新成功')
      } else {
        // 新增模式
        await createTag(tagForm.value)
        message.success('标签添加成功')
      }
      
      showTagModal.value = false
      fetchTagList()
    } catch (error) {
      message.error(editingTag.value ? '标签更新失败' : '标签添加失败')
      console.error(error)
    } finally {
      savingTag.value = false
    }
  })
}

// 删除标签
const handleDeleteTag = async (id: string | number) => {
  try {
    const numericId = typeof id === 'string' ? Number(id) : id
    await deleteTag(numericId)
    message.success('删除成功')
    fetchTagList()
  } catch (error) {
    message.error('删除失败')
    console.error(error)
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCategoryList()
  fetchTagList()
})
</script>

<style scoped>
.categories-tags-management {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.section-card {
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

:deep(.section-card .n-card-header) {
  padding: 20px 20px 12px 20px;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.section-card .n-card__content) {
  padding: 20px;
}

.category-list {
  min-height: 300px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #f9fafb;
  transition: all 0.2s ease;
}

.category-item:hover {
  background-color: #f3f4f6;
  transform: translateX(4px);
}

.category-icon {
  margin-right: 16px;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #e0e7ff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-info {
  flex: 1;
}

.category-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #1f2937;
}

.category-meta {
  display: flex;
  gap: 12px;
}

.category-count {
  font-size: 12px;
  color: #6b7280;
}

.category-status {
  font-size: 12px;
  color: #10b981;
  font-weight: 500;
}

.category-status.disabled {
  color: #9ca3af;
}

.category-actions {
  display: flex;
  align-items: center;
}

.tag-cloud {
  min-height: 300px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-content: flex-start;
}

.tag-item {
  margin: 0;
}

.empty-state {
  text-align: center;
  color: #9ca3af;
  padding: 40px 0;
  font-size: 14px;
}
</style>