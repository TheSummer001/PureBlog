<template>
  <div class="article-edit">
    <!-- 工具栏 -->
    <n-card class="toolbar-card">
      <div class="toolbar">
        <n-input 
          v-model:value="articleForm.title" 
          class="title-input" 
          placeholder="请输入文章标题" 
          size="large"
        />
        <n-button type="primary" @click="handlePublish" :loading="saving">发布</n-button>
        <n-button @click="handleSaveDraft" :loading="saving" style="margin-left: 10px;">保存草稿</n-button>
        <n-button @click="showSettingDrawer = true" style="margin-left: 10px;">设置</n-button>
      </div>
    </n-card>
    
    <!-- 编辑器 -->
    <n-card class="editor-card">
      <MdEditor
        v-model="articleForm.content"
        style="height: 100%"
        @on-upload-img="handleUploadImg"
      />
    </n-card>
    
    <!-- 设置抽屉 -->
    <n-drawer v-model:show="showSettingDrawer" :width="300" placement="right">
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
            
            <n-form-item label="文章分类">
              <n-select 
                v-model:value="articleForm.categoryId" 
                :options="categoryOptions" 
                placeholder="请选择分类"
                filterable
              />
            </n-form-item>
            
            <n-form-item label="文章标签">
              <n-select
                class="tag-selector"
                v-model:value="articleForm.tagIds"
                multiple
                filterable
                tag
                placeholder="请选择或输入标签"
                :options="tagOptions"
                @create="handleCreateTag"
              >
                <template #arrow>
                  <n-button text @click.stop="showTagManager = true">
                    管理标签
                  </n-button>
                </template>
              </n-select>
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
import { NButton, NCard, NInput, NSelect, NForm, NFormItem, NRadioGroup, NRadio, NSwitch, NDrawer, NDrawerContent, useMessage, NModal, NDataTable } from 'naive-ui'
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
      id: detail.id,
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
      value: category.id
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
      value: tag.id
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
}

.toolbar-card {
  margin-bottom: 10px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-input {
  flex: 1;
  margin-right: 20px;
}

.editor-card {
  flex: 1;
}

.setting-content {
  padding: 20px;
}

.tag-selector {
  width: 100%;
}
</style>