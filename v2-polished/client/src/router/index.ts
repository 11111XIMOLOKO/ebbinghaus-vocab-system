import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true, title: '学习总览' } },
    { path: '/login', name: 'login', component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false } },
    { path: '/register', name: 'register', component: () => import('@/views/Register.vue'),
      meta: { requiresAuth: false } },
    { path: '/word-books', name: 'word-books', component: () => import('@/views/WordBooks.vue'),
      meta: { requiresAuth: true, title: '词库中心' } },
    { path: '/study', name: 'study', component: () => import('@/views/Study.vue'),
      meta: { requiresAuth: true, title: '核心背诵' } },
    { path: '/study-plan', name: 'study-plan', component: () => import('@/views/StudyPlan.vue'),
      meta: { requiresAuth: true, title: '学习计划' } },
    { path: '/schedule', name: 'schedule', component: () => import('@/views/Schedule.vue'),
      meta: { requiresAuth: true, title: '复习日程' } },
    { path: '/statistics', name: 'statistics', component: () => import('@/views/Statistics.vue'),
      meta: { requiresAuth: true, title: '数据统计' } },
    { path: '/wrong-words', name: 'wrong-words', component: () => import('@/views/WrongWords.vue'),
      meta: { requiresAuth: true, title: '错词本' } },
    { path: '/admin', name: 'admin', component: () => import('@/views/AdminDashboard.vue'),
      meta: { requiresAuth: true, requiresAdmin: true, title: '后台管理' } },
  ],
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const publicPages = ['login', 'register']

  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'dashboard' })
  } else if (publicPages.includes(to.name as string) && userStore.token) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
