<template>
  <div class="public-layout">
    <header class="header">
      <div class="container">
        <div class="logo">
          <h1>PureBlog</h1>
        </div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/about">关于</router-link>
        </nav>
        <div class="actions">
          <ThemeToggle />
          <router-link to="/admin/article/edit" v-if="userStore.isLoggedIn()">写文章</router-link>
          <router-link to="/login" v-if="!userStore.isLoggedIn()">登录</router-link>
          <router-link to="/admin" v-else>后台管理</router-link>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="container">
        <p>&copy; 2025 PureBlog. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user'
import ThemeToggle from '@/components/ThemeToggle.vue'

const userStore = useUserStore()
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: var(--header-bg, #ffffff);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.nav a {
  margin-right: 20px;
  text-decoration: none;
  color: #333;
  font-weight: 500;
}

.nav a.router-link-active {
  color: #42b983;
}

.actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.actions a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
}

.main {
  flex: 1;
  padding: 20px 0;
}

.footer {
  background: #f5f5f5;
  padding: 20px 0;
  text-align: center;
  margin-top: auto;
}

.footer p {
  margin: 0;
  color: #666;
}
</style>