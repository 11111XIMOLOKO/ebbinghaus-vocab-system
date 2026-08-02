<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="top-bar">
        <h2>📚 艾宾浩斯单词背诵系统</h2>
        <div class="user-info">
          <el-tag v-if="userStore.isAdmin" type="warning" style="margin-right:8px">管理员</el-tag>
          <span>{{ userStore.username }}</span>
          <el-button text @click="handleLogout" style="margin-left:12px">退出</el-button>
        </div>
      </el-header>

      <el-main>
        <!-- 公告轮播 -->
        <el-card v-if="announcements.length > 0" class="notice-bar" shadow="never">
          📢
          <span v-for="(a,i) in announcements" :key="a.id" v-show="noticeIdx===i" class="notice-text">
            {{ a.title }}
          </span>
        </el-card>

        <!-- KPI 卡片 -->
        <el-row :gutter="16" class="kpi-row">
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.newWordCount }}</div>
              <div class="kpi-label">今日新词任务</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.reviewWordCount }}</div>
              <div class="kpi-label">今日待复习</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.checkedIn ? '✅' : '❌' }}</div>
              <div class="kpi-label">今日打卡</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.totalMastered }}</div>
              <div class="kpi-label">累计掌握单词</div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 当前词库信息 -->
        <el-card class="section-card" v-if="overview.hasBook">
          <template #header>当前词库：{{ overview.bookName }}</template>
          <el-button type="primary" size="large" @click="$router.push('/study')">🎯 开始学习新词</el-button>
          <el-button type="warning" size="large" @click="$router.push('/schedule')" style="margin-left:12px">🔄 开始复习</el-button>
        </el-card>
        <el-card class="section-card" v-else>
          <el-result icon="info" title="尚未选择词库" sub-title="请先选择一个词库开始学习">
            <template #extra>
              <el-button type="primary" @click="$router.push('/word-books')">去选词库</el-button>
            </template>
          </el-result>
        </el-card>

        <!-- 快捷入口 -->
        <el-row :gutter="16" class="quick-row">
          <el-col :span="8"><el-card class="quick-card" @click="$router.push('/word-books')">📖 词库浏览</el-card></el-col>
          <el-col :span="8"><el-card class="quick-card" @click="$router.push('/study-plan')">⚙️ 学习计划</el-card></el-col>
          <el-col :span="8"><el-card class="quick-card" @click="$router.push('/statistics')">📊 学习统计</el-card></el-col>
        </el-row>
        <el-row :gutter="16" class="quick-row">
          <el-col :span="8"><el-card class="quick-card" @click="$router.push('/wrong-words')">📝 错词本</el-card></el-col>
          <el-col :span="8"><el-card class="quick-card" @click="handleCheckin">✅ 今日签到</el-card></el-col>
          <el-col :span="8">
            <el-card v-if="userStore.isAdmin" class="quick-card admin-card" @click="$router.push('/admin')">🛡️ 后台管理</el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { logout as apiLogout } from '@/api/auth'
import { getStudyOverview } from '@/api/study'
import request from '@/api/request'
import type { StudyOverviewData } from '@/api/study'

const router = useRouter()
const userStore = useUserStore()

const overview = ref<StudyOverviewData>({
  newWordCount: 0, reviewWordCount: 0, hasBook: false, bookName: '',
  checkedIn: false, totalMastered: 0,
})
const announcements = ref<any[]>([])
const noticeIdx = ref(0)
let noticeTimer: any = null

async function loadData() {
  try {
    const [ov, ann] = await Promise.all([
      getStudyOverview(),
      request.get('/announcements', { params: { pageSize: 5 } })
    ])
    overview.value = ov.data.data
    announcements.value = ann.data.data.records || []
  } catch { /* silent */ }
}

async function handleCheckin() {
  try {
    await request.post('/schedule/checkin')
    ElMessage.success('签到成功！')
    overview.value.checkedIn = true
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '签到失败')
  }
}

async function handleLogout() {
  try { await apiLogout() } catch { /* ignore */ }
  userStore.clearAuth()
  router.push('/login')
}

onMounted(() => {
  loadData()
  noticeTimer = setInterval(() => {
    if (announcements.value.length > 0) {
      noticeIdx.value = (noticeIdx.value + 1) % announcements.value.length
    }
  }, 4000)
})

onUnmounted(() => clearInterval(noticeTimer))
</script>

<style scoped>
.dashboard-container { min-height: 100vh; background: #f5f7fa; }
.top-bar { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 0 24px; height: 64px; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.top-bar h2 { margin: 0; font-size: 20px; }
.user-info { display: flex; align-items: center; }
.notice-bar { margin-bottom: 16px; text-align: center; background: #ecf5ff; }
.notice-text { margin-left: 8px; color: #409eff; }
.kpi-row { margin-bottom: 16px; }
.kpi-card { text-align: center; }
.kpi-num { font-size: 28px; font-weight: 700; color: #409eff; }
.kpi-label { color: #909399; margin-top: 4px; font-size: 13px; }
.section-card { margin-bottom: 16px; text-align: center; }
.quick-row { margin-bottom: 12px; }
.quick-card { text-align: center; cursor: pointer; transition: transform .15s; font-size: 16px; }
.quick-card:hover { transform: translateY(-2px); }
.admin-card { background: #fef0f0; }
</style>
