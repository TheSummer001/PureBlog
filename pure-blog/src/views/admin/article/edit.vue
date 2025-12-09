<template>
  <div class="article-edit">
    <!-- 顶部工具栏 -->
    <n-card class="toolbar-card">
      <div class="toolbar">
        <n-input 
          v-model:value="articleForm.title" 
          placeholder="请输入文章标题" 
          class="title-input"
          maxlength="100"
          show-count
        />
        <div class="toolbar-buttons">
          <n-button @click="handleSaveDraft" :loading="saving" secondary>保存草稿</n-button>
          <n-button @click="handlePublish" type="primary" :loading="saving" style="margin-left: 10px;">发布文章</n-button>
          <n-button @click="showSettingDrawer = true" style="margin-left: 10px;">设置</n-button>
        </div>
      </div>
    </n-card>
    
    <!-- Markdown 编辑器 -->
    <n-card class="editor-card">
      <MdEditor 
        v-model="articleForm.content" 
        :theme="appStore.theme"
        style="height: calc(100vh - 200px)"
        @on-upload-img="handleUploadImg"
      />
    </n-card>
    
    <!-- 设置抽屉 -->
    <n-drawer v-model:show="showSettingDrawer" :width="360" placement="right">
      <n-drawer-content title="文章设置" closable>
        <div class="setting-content">
          <n-form :model="articleForm" label-placement="top">
            <n-form-item label="封面图片">
              <n-input v-model:value="articleForm.coverImg" placeholder="请输入封面图片URL" />
              <!-- TODO: 添加图片上传功能 -->
            </n-form-item>
            <n-form-item label="分类" required>
              <n-select 
                v-model:value="articleForm.categoryId" 
                :options="categoryOptions" 
                placeholder="请选择分类"
                filterable
              />
            </n-form-item>
            <n-form-item label="标签">
              <n-select 
                v-model:value="articleForm.tagIds" 
                :options="tagOptions" 
                placeholder="请选择标签"
                multiple
                filterable
              />
            </n-form-item>
            <n-form-item label="状态">
              <n-radio-group v-model:value="articleForm.status" name="status">
                <n-radio :value="0">草稿</n-radio>
                <n-radio :value="1">已发布</n-radio>
              </n-radio-group>
            </n-form-item>
            <n-form-item label="置顶">
              <n-switch v-model:value="articleForm.isTop" :checked-value="1" :unchecked-value="0" />
            </n-form-item>
          </n-form>
        </div>
        <template #footer>
          <n-button @click="showSettingDrawer = false">关闭</n-button>
        </template>
      </n-drawer-content>
    </n-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { NButton, NCard, NInput, NSelect, NForm, NFormItem, NRadioGroup, NRadio, NSwitch, NDrawer, NDrawerContent, useMessage } from 'naive-ui'
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

// 获取文章详情（编辑模式）
const fetchArticleDetail = async () => {
  if (!articleId.value) return
  
  try {
    const response = await getArticleDetail(articleId.value)
    const detail: ArticleDetailVO = response.data
    
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
    const response = await getCategoryList()
    categoryOptions.value = response.data.map((category: CategoryVO) => ({
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
    const response = await getTagList()
    tagOptions.value = response.data.map((tag: TagVO) => ({
      label: tag.name,
      value: tag.id
    }))
  } catch (error) {
    message.error('获取标签列表失败')
    console.error(error)
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
  
  saving.value = true
  try {
    const response = await saveArticle(articleForm.value)
    message.success(articleId.value ? '文章更新成功' : '文章发布成功')
    
    // 如果是新建文章，跳转到编辑页面
    if (!articleId.value && response.data) {
      router.push(`/admin/article/edit/${response.data}`)
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
</style>