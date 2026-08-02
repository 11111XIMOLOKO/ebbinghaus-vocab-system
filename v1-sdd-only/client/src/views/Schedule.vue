<template>
  <div class="schedule-container">
    <!-- 加载中 -->
    <div v-if="loading" class="center-area">
      <el-icon :size="48" class="is-loading"><Loading /></el-icon>
      <p>加载复习任务...</p>
    </div>

    <!-- 无复习任务 -->
    <div v-else-if="noTasks" class="center-area">
      <el-result icon="success" title="暂无待复习单词">
        <template #sub-title>今日复习任务已完成，继续保持！</template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
          <el-button @click="$router.push('/study')">去学新词</el-button>
        </template>
      </el-result>
    </div>

    <!-- 复习完成 -->
    <div v-else-if="finished" class="center-area">
      <el-result icon="success" title="复习完成！">
        <template #sub-title>
          <p>本次复习了 {{ totalCount }} 个单词</p>
          <p>认识：{{ knownCount }} &nbsp;|&nbsp; 模糊：{{ fuzzyCount }} &nbsp;|&nbsp; 不认识：{{ unknownCount }}</p>
        </template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>

    <!-- 复习卡片 -->
    <div v-else class="review-card-area">
      <div class="progress-bar">
        <span>复习 第 {{ currentIndex + 1 }} / {{ totalCount }} 个</span>
        <el-progress :percentage="progressPercent" :show-text="false" />
      </div>

      <el-card class="word-card" shadow="hover">
        <div class="stage-badge">
          <el-tag type="info" size="small">待复习</el-tag>
        </div>
        <div class="word-english">{{ currentWord?.english }}</div>
        <el-divider />
        <div class="word-chinese">{{ currentWord?.chinese }}</div>
      </el-card>

      <div class="action-buttons">
        <el-button size="large" type="danger" :loading="submitting" @click="handleResult(1)" style="flex:1">
          😕 不认识
        </el-button>
        <el-button size="large" type="warning" :loading="submitting" @click="handleResult(2)" style="flex:1">
          🤔 模糊
        </el-button>
        <el-button size="large" type="success" :loading="submitting" @click="handleResult(3)" style="flex:1">
          😊 认识
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getDueReviews, type WordData } from '@/api/schedule'
import { submitResult } from '@/api/study'

const loading = ref(true)
const submitting = ref(false)
const finished = ref(false)
const noTasks = ref(false)

const words = ref<WordData[]>([])
const currentIndex = ref(0)
const knownCount = ref(0)
const fuzzyCount = ref(0)
const unknownCount = ref(0)
const totalCount = ref(0)

const currentWord = computed(() =>
  currentIndex.value < words.value.length ? words.value[currentIndex.value] : null
)
const progressPercent = computed(() =>
  totalCount.value > 0 ? Math.round((currentIndex.value / totalCount.value) * 100) : 0
)

async function loadWords() {
  loading.value = true
  noTasks.value = false
  finished.value = false
  try {
    const res = await getDueReviews()
    words.value = res.data.data
    totalCount.value = words.value.length
    currentIndex.value = 0
    knownCount.value = 0
    fuzzyCount.value = 0
    unknownCount.value = 0
    if (words.value.length === 0) {
      noTasks.value = true
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '获取复习任务失败')
    noTasks.value = true
  } finally {
    loading.value = false
  }
}

async function handleResult(familiarity: number) {
  if (!currentWord.value || submitting.value) return

  submitting.value = true
  try {
    await submitResult(currentWord.value.id, familiarity)
    if (familiarity === 3) knownCount.value++
    else if (familiarity === 2) fuzzyCount.value++
    else unknownCount.value++

    currentIndex.value++
    if (currentIndex.value >= totalCount.value) {
      finished.value = true
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadWords)
</script>

<style scoped>
.schedule-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f5ff 0%, #e8f0fe 100%);
}

.center-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 16px;
}

.review-card-area {
  max-width: 520px;
  margin: 0 auto;
  padding: 40px 20px;
}

.progress-bar {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
}

.progress-bar .el-progress {
  flex: 1;
}

.word-card {
  text-align: center;
  min-height: 240px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stage-badge {
  margin-bottom: 12px;
}

.word-english {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  padding: 20px 0 10px;
}

.word-chinese {
  font-size: 22px;
  color: #909399;
  padding: 10px 0 20px;
}

.action-buttons {
  margin-top: 32px;
  display: flex;
  gap: 12px;
}
</style>
