<template>
  <div class="login-page">
    <n-card class="login-card" title="用户登录">
      <n-form
        ref="formRef"
        :model="loginForm"
        :rules="loginRules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
      >
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="loginForm.username"
            placeholder="请输入用户名"
            @keydown.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="loginForm.password"
            type="password"
            placeholder="请输入密码"
            @keydown.enter="handleLogin"
          />
        </n-form-item>
        <n-row :gutter="[0, 24]">
          <n-col :span="24">
            <div style="display: flex; justify-content: space-between">
              <n-checkbox v-model:checked="rememberMe">记住我</n-checkbox>
              <n-button text>忘记密码？</n-button>
            </div>
          </n-col>
          <n-col :span="24">
            <n-button
              type="primary"
              block
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </n-button>
          </n-col>
        </n-row>
      </n-form>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
// ✅ 新增：导入所需的 Naive UI 组件
import { 
  NCard, 
  NForm, 
  NFormItem, 
  NInput, 
  NButton, 
  NCheckbox, 
  NRow, 
  NCol, 
  type FormInst, 
  type FormRules 
} from 'naive-ui'

interface LoginForm {
  username: string
  password: string
}

const formRef = ref<FormInst | null>(null)
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive<LoginForm>({
  username: 'admin', // 预填方便测试
  password: 'admin123'
})

const loginRules: FormRules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },
    {
      min: 5, // 根据后端逻辑调整，原代码写6可能会导致 admin123 报错
      message: '密码长度至少5位',
      trigger: 'blur'
    }
  ]
}

const handleLogin = (e: Event) => {
  e.preventDefault()
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      loading.value = true
      try {
        // 调用登录接口
        await userStore.login({
          username: loginForm.username,
          password: loginForm.password
        })

        // 登录成功后跳转
        const redirect = route.query.redirect as string || '/admin'
        router.push(redirect)
      } catch (error: any) {
        // 登录失败处理已在 request.ts 中统一弹出 message
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  /* 确保背景色不是透明的 */
  background-color: white; 
}
</style>