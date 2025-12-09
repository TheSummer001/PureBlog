<template>
  <n-layout has-sider class="admin-layout">
    <!-- 侧边栏 -->
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="logo">
        <h2 v-if="!collapsed">Admin Panel</h2>
        <h2 v-else>AP</h2>
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <!-- 主内容区 -->
    <n-layout>
      <!-- 头部 -->
      <n-layout-header bordered>
        <div class="header">
          <n-breadcrumb>
            <n-breadcrumb-item v-for="item in breadcrumbs" :key="item">
              {{ item }}
            </n-breadcrumb-item>
          </n-breadcrumb>
          <div class="user-actions">
            <n-dropdown :options="userDropdownOptions" @select="handleUserAction">
              <n-button quaternary>
                {{ userStore.userInfo?.nickname || userStore.userInfo?.username || '未知用户' }}
                <template #icon>
                  <n-icon>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
                      <path fill="currentColor" d="M12 2a5 5 0 1 0 5 5a5 5 0 0 0-5-5zm0 8a3 3 0 1 1 3-3a3 3 0 0 1-3 3zm9 11v-1a7 7 0 0 0-7-7h-4a7 7 0 0 0-7 7v1h2v-1a5 5 0 0 1 5-5h4a5 5 0 0 1 5 5v1z"/>
                    </svg>
                  </n-icon>
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
import { NIcon } from 'naive-ui'

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

const DashboardIcon = '<path fill="currentColor" d="M4 3h16a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1m1 2v14h14V5z"/><path fill="currentColor" d="M4 3h16a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1m1 2v14h14V5z"/><path fill="currentColor" d="M5 5h4v4H5zm5 0h4v4h-4zm5 0h4v4h-4zm-10 5h4v4H5zm5 0h4v4h-4zm5 0h4v4h-4zm-10 5h4v4H5zm5 0h4v4h-4zm5 0h4v4h-4z"/>'
const ArticleIcon = '<path fill="currentColor" d="M4 4h16v16H4zm2 2v12h12V6zm2 2h8v2H8zm0 4h8v2H8zm0 4h5v2H8z"/>'
const CategoryIcon = '<path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87l1.18 6.88L12 17.77l-6.18 3.25L7 14.14l-5-4.87l6.91-1.01z"/>'
const TagIcon = '<path fill="currentColor" d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58c.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41c0-.55-.23-1.06-.59-1.42M5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7"/>'
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

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #eee;
}

.logo h2 {
  margin: 0;
  font-size: 1.2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.user-actions {
  display: flex;
  align-items: center;
}

.content {
  padding: 20px;
  height: calc(100% - 60px);
  overflow: auto;
}
</style>