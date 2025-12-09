<template>
  <div class="settings">
    <h1>系统设置</h1>
    <n-tabs type="line" animated>
      <n-tab-pane name="basic" tab="基本设置">
        <n-form
          ref="formRef"
          :model="settingsForm"
          :rules="settingsRules"
          label-placement="left"
          label-width="120px"
        >
          <n-form-item label="网站标题" path="siteTitle">
            <n-input
              v-model:value="settingsForm.siteTitle"
              placeholder="请输入网站标题"
            />
          </n-form-item>
          <n-form-item label="网站描述" path="siteDescription">
            <n-input
              v-model:value="settingsForm.siteDescription"
              type="textarea"
              placeholder="请输入网站描述"
            />
          </n-form-item>
          <n-form-item label="备案号" path="beianNumber">
            <n-input
              v-model:value="settingsForm.beianNumber"
              placeholder="请输入备案号"
            />
          </n-form-item>
          <n-form-item label="每页文章数" path="postsPerPage">
            <n-input-number
              v-model:value="settingsForm.postsPerPage"
              :min="1"
              :max="100"
            />
          </n-form-item>
          <n-form-item label="允许评论" path="enableComments">
            <n-switch v-model:value="settingsForm.enableComments" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="saveSettings">保存设置</n-button>
          </n-form-item>
        </n-form>
      </n-tab-pane>
      <n-tab-pane name="seo" tab="SEO设置">
        <n-alert title="提示" type="info">
          SEO设置可以帮助搜索引擎更好地索引您的网站。
        </n-alert>
      </n-tab-pane>
      <n-tab-pane name="email" tab="邮箱设置">
        <n-alert title="提示" type="info">
          邮箱设置用于发送通知邮件。
        </n-alert>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInst, FormRules } from 'naive-ui'

interface SettingsForm {
  siteTitle: string
  siteDescription: string
  beianNumber: string
  postsPerPage: number
  enableComments: boolean
}

const formRef = ref<FormInst | null>(null)

const settingsForm = reactive<SettingsForm>({
  siteTitle: 'PureBlog',
  siteDescription: '一个简洁的个人博客系统',
  beianNumber: '',
  postsPerPage: 10,
  enableComments: true
})

const settingsRules: FormRules = {
  siteTitle: [
    {
      required: true,
      message: '请输入网站标题',
      trigger: 'blur'
    }
  ],
  postsPerPage: [
    {
      required: true,
      type: 'number',
      message: '请输入每页文章数',
      trigger: 'blur'
    }
  ]
}

const saveSettings = () => {
  formRef.value?.validate((errors) => {
    if (!errors) {
      // 保存设置逻辑
      console.log('保存设置:', settingsForm)
    }
  })
}
</script>

<style scoped>
.settings {
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
}
</style>