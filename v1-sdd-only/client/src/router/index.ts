import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/word-books',
      name: 'word-books',
      component: () => import('@/views/WordBooks.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/study',
      name: 'study',
      component: () => import('@/views/Study.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/study-plan',
      name: 'study-plan',
      component: () => import('@/views/StudyPlan.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/schedule',
      name: 'schedule',
      component: () => import('@/views/Schedule.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('@/views/Statistics.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/wrong-words',
      name: 'wrong-words',
      component: () => import('@/views/WrongWords.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminDashboard.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const publicPages = ['login', 'register']

  if (to.meta.requiresAuth && !userStore.token) {
    // 未登录，跳转登录页，带上原目标路径
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (publicPages.includes(to.name as string) && userStore.token) {
    // 已登录用户访问登录/注册页，跳转首页
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
