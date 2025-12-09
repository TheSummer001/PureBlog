<template>
  <n-layout has-sider class="admin-layout">
    <!-- 侧边栏 -->
    <n-layout-sider
      class="sidebar"
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="logo">
        <h2 v-if="!collapsed" class="logo-text">Blog Admin</h2>
        <h2 v-else class="logo-text-collapsed">BA</h2>
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
        class="menu"
      />
    </n-layout-sider>

    <!-- 主内容区 -->
    <n-layout>
      <!-- 头部 -->
      <n-layout-header bordered class="header">
        <div class="header-content">
          <n-breadcrumb>
            <n-breadcrumb-item v-for="item in breadcrumbs" :key="item">
              {{ item }}
            </n-breadcrumb-item>
          </n-breadcrumb>
          <div class="user-actions">
            <n-button 
              type="primary" 
              size="small" 
              class="write-btn"
              @click="handleWriteArticle"
            >
              <template #icon>
                <n-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
                    <path fill="currentColor" d="m14.06 9.02l.92.92L5.92 19H5v-.92l9.06-9.06M17.66 3c-.25 0-.51.1-.7.29l-1.83 1.83l3.75 3.75l1.83-1.83a.996.996 0 0 0 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29zm-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z"/>
                  </svg>
                </n-icon>
              </template>
              写文章
            </n-button>
            <n-dropdown :options="userDropdownOptions" @select="handleUserAction">
              <n-button quaternary circle class="avatar-btn">
                <template #icon>
                  <img 
                    :src="`https://api.dicebear.com/7.x/initials/svg?seed=${userStore.userInfo?.nickname || userStore.userInfo?.username || 'U'}`" 
                    alt="Avatar"
                    class="avatar"
                  />
                </template>
              </n-button>
            </n-dropdown>
          </div>
        </div>
      </n-layout-header>

      <!-- 内容区域 -->
      <n-layout-content class="content">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import type { MenuOption } from 'naive-ui'
import { NIcon, NButton } from 'naive-ui'

// 图标组件
const renderIcon = (icon: string) => {
  return () => h(NIcon, null, {
    default: () => h('svg', {
      xmlns: 'http://www.w3.org/2000/svg',
      width: '16',
      height: '16',
      viewBox: '0 0 24 24',
      innerHTML: icon
    })
  })
}

const DashboardIcon = '<path fill="currentColor" d="M13 3L4 9l7 5l9-6l-7-5m0 2.3l5.4 3.8l-5.4 3.7L7.6 8.1zM4 19l7 5l9-6l-9-5l-7 6z"/>'
const ArticleIcon = '<path fill="currentColor" d="M14 17H4v2h10v-2zm6-8H4v2h16V9zM4 15h16v-2H4v2zM4 5v2h16V5H4z"/>'
const CategoryIcon = '<path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87l1.18 6.88L12 17.77l-6.18 3.25L7 14.14l-5-4.87l6.91-1.01L12 2z"/>'
const TagIcon = '<path fill="currentColor" d="M17.84 14.69l-2.53 2.54l-4.27-4.27l2.53-2.53l4.27 4.26m-2.12 2.13l-1.41 1.41l-4.27-4.27l1.41-1.41l4.27 4.27M12 2C8.14 2 5 5.14 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.86-3.14-7-7-7z"/>'
const SettingsIcon = '<path fill="currentColor" d="M12 15.5A3.5 3.5 0 0 1 8.5 12A3.5 3.5 0 0 1 12 8.5a3.5 3.5 0 0 1 3.5 3.5a3.5 3.5 0 0 1-3.5 3.5m7.43-2.53c.04-.32.07-.64.07-.97c0-.33-.03-.66-.07-1l2.11-1.63c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.31-.61-.22l-2.49 1c-.52-.39-1.06-.73-1.69-.98l-.37-2.65A.506.506 0 0 0 14 2h-4c-.25 0-.46.18-.5.42l-.37 2.65c-.63.25-1.17.59-1.69.98l-2.49-1c-.22-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64L4.57 11c-.04.34-.07.67-.07 1c0 .33.03.65.07.97l-2.11 1.66c-.19.15-.25.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1.01c.52.4 1.06.74 1.69.99l.37 2.65c.04.24.25.42.5.42h4c.25 0 .46-.18.5-.42l.37-2.65c.63-.26 1.17-.59 1.69-.99l2.49 1.01c.22.08.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.66Z"/>'

const collapsed = ref(false)
const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 菜单选项
const menuOptions: MenuOption[] = [
  {
    label: '仪表盘',
    key: 'dashboard',
    icon: renderIcon(DashboardIcon)
  },
  {
    label: '文章管理',
    key: 'articles',
    icon: renderIcon(ArticleIcon)
  },
  {
    label: '分类管理',
    key: 'categories',
    icon: renderIcon(CategoryIcon)
  },
  {
    label: '标签管理',
    key: 'tags',
    icon: renderIcon(TagIcon)
  },
  {
    label: '系统设置',
    key: 'settings',
    icon: renderIcon(SettingsIcon)
  }
]

// 当前激活的菜单项
const activeKey = computed(() => {
  const path = route.path
  if (path.includes('dashboard')) return 'dashboard'
  if (path.includes('articles')) return 'articles'
  if (path.includes('categories')) return 'categories'
  if (path.includes('tags')) return 'tags'
  if (path.includes('settings')) return 'settings'
  return 'dashboard'
})

// 面包屑导航
const breadcrumbs = computed(() => {
  const path = route.path
  const crumbs = ['首页']
  
  if (path.includes('dashboard')) crumbs.push('仪表盘')
  if (path.includes('articles')) crumbs.push('文章管理')
  if (path.includes('categories')) crumbs.push('分类管理')
  if (path.includes('tags')) crumbs.push('标签管理')
  if (path.includes('settings')) crumbs.push('系统设置')
  
  return crumbs
})

// 用户下拉菜单选项
const userDropdownOptions = [
  {
    label: '个人信息',
    key: 'profile'
  },
  {
    label: '退出登录',
    key: 'logout'
  }
]

// 处理菜单选择
const handleMenuSelect = (key: string) => {
  switch (key) {
    case 'dashboard':
      router.push('/admin/dashboard')
      break
    case 'articles':
      router.push('/admin/articles')
      break
    case 'categories':
      router.push('/admin/categories')
      break
    case 'tags':
      router.push('/admin/tags')
      break
    case 'settings':
      router.push('/admin/settings')
      break
  }
}

// 处理用户操作
const handleUserAction = (key: string) => {
  if (key === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

// 处理写文章操作
const handleWriteArticle = () => {
  router.push('/admin/article/edit')
}

// 获取用户信息
onMounted(async () => {
  if (!userStore.userInfo && userStore.token) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      userStore.logout()
      router.push('/login')
    }
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background-color: white;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #eee;
}

.logo-text {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.logo-text-collapsed {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.menu :deep(.n-menu-item) {
  border-radius: 0.5rem;
  margin: 0.25rem 0.75rem;
  transition: all 0.2s ease;
}

.menu :deep(.n-menu-item.n-menu-item--selected) {
  background-color: rgba(79, 70, 229, 0.1); /* bg-primary/10 */
  position: relative;
}

.menu :deep(.n-menu-item.n-menu-item--selected)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: #4f46e5; /* primary purple */
  border-radius: 0 4px 4px 0;
}

.menu :deep(.n-menu-item-content) {
  padding: 0.75rem 1rem !important;
}

.menu :deep(.n-menu-item-content .n-menu-item-content__icon) {
  font-size: 1.25rem;
  margin-right: 0.75rem;
}

.menu :deep(.n-menu-item-content .n-menu-item-content__text) {
  font-weight: 500;
}

.menu :deep(.n-menu-item.n-menu-item--selected .n-menu-item-content__text) {
  color: #4f46e5; /* primary purple */
}

.header {
  background-color: white;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 1.5rem;
}

.write-btn {
  margin-right: 1rem;
}

.avatar-btn {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.content {
  padding: 1.5rem;
  background-color: #f3f4f6; /* bg-gray-100 */
  height: calc(100% - 60px);
  overflow: auto;
}

/* 自定义滚动条样式 */
.content::-webkit-scrollbar {
  width: 6px;
}

.content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.content::-webkit-scrollbar-thumb {
  background: #c5c5c5;
  border-radius: 3px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>