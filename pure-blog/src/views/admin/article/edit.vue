<template>
  <div class="article-edit">
    <!-- 顶部工具栏 -->
    <div class="editor-header">
      <n-input 
        v-model:value="articleForm.title" 
        class="title-input" 
        placeholder="请输入文章标题" 
        size="large"
      />
      <div class="header-actions">
        <n-button @click="handleSaveDraft" :loading="saving" class="draft-btn">保存草稿</n-button>
        <n-button type="primary" @click="handlePublish" :loading="saving" class="publish-btn">发布</n-button>
      </div>
    </div>
    
    <!-- 属性栏 -->
    <div class="attribute-bar">
      <div class="attribute-item">
        <span class="attribute-label">分类</span>
        <n-select 
          v-model:value="articleForm.categoryId" 
          :options="categoryOptions" 
          placeholder="请选择分类"
          filterable
          size="small"
          class="attribute-select"
        />
      </div>
      
      <div class="attribute-item">
        <span class="attribute-label">标签</span>
        <n-select
          class="tag-selector"
          v-model:value="articleForm.tagIds"
          multiple
          filterable
          tag
          placeholder="请选择或输入标签"
          :options="tagOptions"
          @create="handleCreateTag"
          size="small"
        >
          <template #arrow>
            <n-button text @click.stop="showTagManager = true" size="tiny">
              管理标签
            </n-button>
          </template>
        </n-select>
      </div>
      
      <div class="attribute-item">
        <n-button @click="showSettingDrawer = true" size="small" quaternary>
          <template #icon>
            <n-icon>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="currentColor" d="M12 15.5A3.5 3.5 0 0 1 8.5 12A3.5 3.5 0 0 1 12 8.5a3.5 3.5 0 0 1 3.5 3.5a3.5 3.5 0 0 1-3.5 3.5m7.43-2.53c.04-.32.07-.64.07-.97c0-.33-.03-.66-.07-1l2.11-1.63c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.31-.61-.22l-2.49 1c-.52-.39-1.06-.73-1.69-.98l-.37-2.65A.506.506 0 0 0 14 2h-4c-.25 0-.46.18-.5.42l-.37 2.65c-.63.25-1.17.59-1.69.98l-2.49-1c-.22-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64L4.57 11c-.04.34-.07.67-.07 1c0 .33.03.65.07.97l-2.11 1.66c-.19.15-.25.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1.01c.52.4 1.06.74 1.69.99l.37 2.65c.04.24.25.42.5.42h4c.25 0 .46-.18.5-.42l.37-2.65c.63-.26 1.17-.59 1.69-.99l2.49 1.01c.22.08.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.66Z"/></svg>
            </n-icon>
          </template>
          设置
        </n-button>
      </div>
    </div>
    
    <!-- 编辑器主体 -->
    <div class="editor-body">
      <MdEditor
        v-model="articleForm.content"
        style="height: 100%"
        @on-upload-img="handleUploadImg"
      />
    </div>
    
    <!-- 设置抽屉 -->
    <n-drawer v-model:show="showSettingDrawer" :width="350" placement="right">
      <n-drawer-content title="文章设置" closable>
        <div class="setting-content">
          <n-form>
            <n-form-item label="文章摘要">
              <n-input 
                v-model:value="articleForm.summary" 
                type="textarea" 
                placeholder="请输入文章摘要"
                :autosize="{ minRows: 3, maxRows: 6 }"
              />
            </n-form-item>
            
            <n-form-item label="封面图片">
              <n-input v-model:value="articleForm.coverImg" placeholder="请输入封面图片URL" />
            </n-form-item>
            
            <n-form-item label="是否置顶">
              <n-switch v-model:value="articleForm.isTop" :checked-value="1" :unchecked-value="0" />
            </n-form-item>
          </n-form>
        </div>
      </n-drawer-content>
    </n-drawer>
    
    <!-- 标签管理弹窗 -->
    <n-modal v-model:show="showTagManager" preset="card" title="标签管理" style="width: 600px;">
      <n-form inline>
        <n-form-item label="新标签名称">
          <n-input v-model:value="newTagName" placeholder="请输入标签名称" />
        </n-form-item>
        <n-form-item>
          <n-button @click="addNewTag">添加</n-button>
        </n-form-item>
      </n-form>
      
      <n-data-table
        :columns="tagColumns"
        :data="tagOptions"
        :bordered="false"
        size="small"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { NButton, NInput, NSelect, NForm, NFormItem, NSwitch, NDrawer, NDrawerContent, useMessage, NModal, NDataTable, NIcon } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { useAppStore } from '@/store/app'
import { useImageUpload } from '@/hooks/useImageUpload'
import { getArticleDetail, saveArticle } from '@/api/article'
import { getCategoryList, getTagList } from '@/api/article'
import type { ArticleDetailVO, ArticleSubmitDTO } from '@/types/article'
import type { CategoryVO } from '@/types/category'
import type { TagVO } from '@/types/tag'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const appStore = useAppStore()
const { uploadImage } = useImageUpload()

// 文章ID（如果是编辑模式）
const articleId = computed(() => route.params.id ? Number(route.params.id) : null)

// 抽屉显示状态
const showSettingDrawer = ref(false)

// 标签管理弹窗显示状态
const showTagManager = ref(false)

// 新标签名称
const newTagName = ref('')

// 保存状态
const saving = ref(false)

// 文章表单数据
const articleForm = ref<ArticleSubmitDTO>({
  title: '',
  content: '',
  summary: '',
  coverImg: '',
  categoryId: 0,
  status: 1,
  isTop: 0,
  tagIds: []
})

// 分类选项
const categoryOptions = ref<{ label: string; value: number }[]>([])

// 标签选项
const tagOptions = ref<{ label: string; value: number }[]>([])

// 标签表格列定义
const tagColumns: DataTableColumns<{ label: string; value: number }> = [
  {
    title: '标签名称',
    key: 'label'
  },
  {
    title: '操作',
    key: 'actions',
    render(row) {
      return h(
        NButton,
        {
          size: 'small',
          type: 'error',
          onClick: () => removeTag(row.value)
        },
        { default: () => '删除' }
      )
    }
  }
]

// 获取文章详情（编辑模式）
const fetchArticleDetail = async () => {
  if (!articleId.value) return
  
  try {
    const detail: ArticleDetailVO = await getArticleDetail(articleId.value)
    
    // 填充表单数据
    articleForm.value = {
      id: Number(detail.id), // 将字符串类型的id转换为数字
      title: detail.title,
      content: detail.content,
      summary: detail.summary,
      coverImg: detail.coverImg,
      categoryId: detail.category.id,
      status: detail.status,
      isTop: detail.isTop,
      tagIds: detail.tags.map(tag => tag.id)
    }
  } catch (error) {
    message.error('获取文章详情失败')
    console.error(error)
  }
}

// 获取分类列表
const fetchCategoryList = async () => {
  try {
    const response: CategoryVO[] = await getCategoryList()
    categoryOptions.value = response.map((category: CategoryVO) => ({
      label: category.name,
      value: Number(category.id) // 将字符串类型的id转换为数字
    }))
  } catch (error) {
    message.error('获取分类列表失败')
    console.error(error)
  }
}

// 获取标签列表
const fetchTagList = async () => {
  try {
    const response: TagVO[] = await getTagList()
    tagOptions.value = response.map((tag: TagVO) => ({
      label: tag.name,
      value: typeof tag.id === 'string' ? Number(tag.id) : tag.id // 处理string|number类型
    }))
  } catch (error) {
    message.error('获取标签列表失败')
    console.error(error)
  }
}

// 处理创建新标签
const handleCreateTag = (label: string) => {
  // 使用负数作为临时ID，避免与真实ID冲突
  let tempId = -(tagOptions.value.length + 1);
  // 确保临时ID唯一
  while (tagOptions.value.some(tag => tag.value === tempId)) {
    tempId--;
  }
  
  tagOptions.value.push({ label, value: tempId })
  if (!articleForm.value.tagIds) {
    articleForm.value.tagIds = []
  }
  articleForm.value.tagIds.push(tempId)
  return {
    label,
    value: tempId
  }
}

// 添加新标签
const addNewTag = () => {
  if (!newTagName.value.trim()) {
    message.warning('请输入标签名称')
    return
  }
  
  // 检查标签是否已存在
  const exists = tagOptions.value.some(tag => tag.label === newTagName.value.trim())
  if (exists) {
    message.warning('标签已存在')
    return
  }
  
  // 使用负数作为临时ID，避免与真实ID冲突
  let tempId = -(tagOptions.value.length + 1);
  // 确保临时ID唯一
  while (tagOptions.value.some(tag => tag.value === tempId)) {
    tempId--;
  }
  
  tagOptions.value.push({ label: newTagName.value.trim(), value: tempId })
  newTagName.value = ''
  message.success('标签添加成功')
}

// 删除标签
const removeTag = (value: number) => {
  tagOptions.value = tagOptions.value.filter(tag => tag.value !== value)
  if (articleForm.value.tagIds) {
    articleForm.value.tagIds = articleForm.value.tagIds.filter(id => id !== value)
  }
}

// 处理图片上传
const handleUploadImg = async (files: File[], callback: (urls: string[]) => void) => {
  try {
    const urls = await uploadImage(files)
    callback(urls)
  } catch (error) {
    message.error('图片上传失败')
    console.error(error)
    callback([])
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  articleForm.value.status = 0 // 草稿状态
  await saveArticleFunc()
}

// 发布文章
const handlePublish = async () => {
  articleForm.value.status = 1 // 发布状态
  await saveArticleFunc()
}

// 保存文章（通用方法）
const saveArticleFunc = async () => {
  // 表单验证
  if (!articleForm.value.title.trim()) {
    message.warning('请输入文章标题')
    return
  }
  
  if (!articleForm.value.content.trim()) {
    message.warning('请输入文章内容')
    return
  }
  
  if (!articleForm.value.categoryId) {
    message.warning('请选择文章分类')
    return
  }
  
  // 注意：标签不再是必需的，所以不进行验证
  
  saving.value = true
  try {
    const response: number = await saveArticle(articleForm.value)
    message.success(articleId.value ? '文章更新成功' : '文章发布成功')
    
    // 如果是新建文章，跳转到仪表盘；如果是编辑文章，则留在当前页面
    if (!articleId.value && response) {
      router.push('/admin/dashboard')
    }
  } catch (error) {
    message.error(articleId.value ? '文章更新失败' : '文章发布失败')
    console.error(error)
  } finally {
    saving.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCategoryList()
  fetchTagList()
  
  // 如果是编辑模式，获取文章详情
  if (articleId.value) {
    fetchArticleDetail()
  }
})
</script>

<style scoped>
.article-edit {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f9fafb;
}

.editor-header {
  padding: 20px;
  background-color: white;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-input {
  flex: 1;
}

.title-input :deep(.n-input__input-el) {
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.draft-btn {
  background-color: #f3f4f6;
  border-color: #e5e7eb;
  color: #374151;
}

.publish-btn {
  min-width: 80px;
}

.attribute-bar {
  padding: 12px 20px;
  background-color: white;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 24px;
}

.attribute-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.attribute-label {
  font-size: 14px;
  color: #6b7280;
  white-space: nowrap;
}

.attribute-select {
  min-width: 150px;
}

.tag-selector {
  min-width: 200px;
}

.editor-body {
  flex: 1;
  padding: 20px;
}

.setting-content {
  padding: 20px;
}

:deep(.md-editor) {
  border-radius: 8px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

:deep(.md-editor-toolbar-wrapper) {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

:deep(.md-editor-preview-wrapper) {
  padding: 20px;
}
</style>