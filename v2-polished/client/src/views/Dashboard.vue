<template>
  <div class="page-grid">
    <!-- KPI 卡片 -->
    <el-row :gutter="18">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card class="stat-card">
          <div class="muted">{{ item.label }}</div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="muted">{{ item.tip }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告轮播 -->
    <el-card v-if="notices.length" class="stat-card">
      <el-carousel height="100px" :interval="4000" indicator-position="outside" arrow="never">
        <el-carousel-item v-for="a in notices" :key="a.id">
          <div style="text-align:center; padding:0 40px">
            <el-tag size="small" round>{{ a.type || '公告' }}</el-tag>
            <h3 style="margin:8px 0 4px">{{ a.title }}</h3>
            <p class="muted">{{ a.content }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-card>

    <!-- 当前词库 + 操作 -->
    <el-row :gutter="18">
      <el-col :span="12">
        <el-card class="stat-card">
          <template #header>
            <span>当前学习计划</span>
          </template>
          <div v-if="ov.hasBook">
            <p>词库：<strong>{{ ov.bookName }}</strong></p>
            <p>每日新词：{{ ov.newWordCount }} 个 &nbsp;|&nbsp; 待复习：{{ ov.reviewWordCount }} 个</p>
            <div style="margin-top:12px">
              <el-button type="primary" @click="$router.push('/study')">🎯 开始学习</el-button>
              <el-button @click="$router.push('/schedule')">🔄 开始复习</el-button>
            </div>
          </div>
          <div v-else>
            <p class="muted">尚未选择词库</p>
            <el-button type="primary" @click="$router.push('/word-books')">去选词库</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="stat-card">
          <template #header><span>快捷操作</span></template>
          <div style="display:flex; flex-wrap:wrap; gap:10px">
            <el-button @click="$router.push('/word-books')">📖 词库</el-button>
            <el-button @click="$router.push('/study-plan')">⚙️ 计划</el-button>
            <el-button @click="$router.push('/statistics')">📊 统计</el-button>
            <el-button @click="$router.push('/wrong-words')">📝 错词</el-button>
            <el-button @click="handleCheckin">✅ 签到</el-button>
            <el-button v-if="userStore.isAdmin" @click="$router.push('/admin')">🛡️ 管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const userStore = useUserStore()
const notices = ref<any[]>([])
const ov = ref({ newWordCount:0, reviewWordCount:0, hasBook:false, bookName:'', checkedIn:false, totalMastered:0 })
const mastered = ref(0)

const stats = computed(() => [
  { label: '今日新词', value: ov.value.newWordCount, tip: '待学习' },
  { label: '待复习', value: ov.value.reviewWordCount, tip: '到期词' },
  { label: '连续打卡', value: ov.value.checkedIn ? '✅' : '—', tip: '今日' },
  { label: '已掌握', value: mastered.value, tip: '累计' },
])

async function load() {
  try {
    const [s, a, stat] = await Promise.all([
      request.get('/study/overview'),
      request.get('/announcements', { params: { pageSize: 5 } }),
      request.get('/statistics/overview'),
    ])
    ov.value = s.data.data
    notices.value = a.data.data.records || []
    mastered.value = stat.data.data.totalMastered || 0
  } catch { /* silent */ }
}

async function handleCheckin() {
  try { await request.post('/schedule/checkin'); ElMessage.success('签到成功！'); ov.value.checkedIn = true }
  catch (e: any) { ElMessage.error(e.response?.data?.message || '签到失败') }
}

onMounted(load)
</script>
