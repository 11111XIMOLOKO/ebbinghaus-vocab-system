<template>
  <router-view v-if="isAuthPage && ready" />
  <div v-else-if="!ready" class="loading">加载中...</div>
  <el-container v-else class="app-shell">
    <el-aside width="248px" class="sidebar">
      <div class="brand">
        <div class="brand-logo">E</div>
        <div><h1>艾宾浩斯</h1><p>Vocabulary System</p></div>
      </div>
      <el-menu router :default-active="$route.path" class="nav-menu">
        <el-menu-item index="/">学习总览</el-menu-item>
        <el-menu-item index="/word-books">词库中心</el-menu-item>
        <el-menu-item index="/study">核心背诵</el-menu-item>
        <el-menu-item index="/schedule">复习日程</el-menu-item>
        <el-menu-item index="/wrong-words">错词本</el-menu-item>
        <el-menu-item index="/statistics">数据统计</el-menu-item>
        <el-menu-item index="/study-plan">学习计划</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin">后台管理</el-menu-item>
      </el-menu>

      <div style="position:absolute; bottom:16px; left:10px; right:10px;">
        <el-button style="width:100%" @click="handleLogout">退出登录</el-button>
      </div>
    </el-aside>

    <el-container class="main-shell">
      <el-header class="topbar">
        <div>
          <strong>{{ $route.meta.title || '艾宾浩斯单词背诵系统' }}</strong>
          <span class="topbar-subtitle">基于遗忘曲线的智能复习规划</span>
        </div>
        <div class="topbar-actions">
          <el-button type="primary" @click="$router.push('/schedule')">今日打卡</el-button>
          <div class="user-panel">
            <span class="user-label">当前用户</span>
            <span class="user-entry">{{ userStore.username }}</span>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMe } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const ready = ref(false)
const isAuthPage = computed(() => route.path === '/login' || route.path === '/register')
const isAdmin = computed(() => userStore.role === 'ADMIN')

onMounted(async () => {
  if (userStore.token) {
    try { await getMe() } catch { userStore.clearAuth() }
  }
  ready.value = true
})

async function handleLogout() {
  userStore.clearAuth()
  router.push('/login')
}
</script>

<style>
.loading { display:flex; justify-content:center; align-items:center;
  min-height:100vh; font-size:18px; color:#909399; }
</style>
