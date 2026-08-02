<template>
  <div class="study-container">
    <!-- 加载中 -->
    <div v-if="loading" class="study-center">
      <el-icon :size="48" class="is-loading"><Loading /></el-icon>
      <p>正在准备单词...</p>
    </div>

    <!-- 学习完成 -->
    <div v-else-if="finished" class="study-center">
      <el-result icon="success" title="学习完成！">
        <template #sub-title>
          <p>本次学习了 {{ totalCount }} 个单词</p>
          <p>认识：{{ knownCount }} &nbsp;|&nbsp; 不认识：{{ unknownCount }}</p>
        </template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
          <el-button @click="startNewRound">再学一批</el-button>
        </template>
      </el-result>
    </div>

    <!-- 无单词可学 -->
    <div v-else-if="noWords" class="study-center">
      <el-result icon="info" title="没有可学的单词">
        <template #sub-title>{{ errorMsg }}</template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/word-books')">去选词库</el-button>
        </template>
      </el-result>
    </div>

    <!-- 学习卡片 -->
    <div v-else class="study-card-area">
      <div class="progress-bar">
        <span>第 {{ currentIndex + 1 }} / {{ totalCount }} 个</span>
        <el-progress :percentage="progressPercent" :show-text="false" />
      </div>

      <el-card class="word-card" shadow="hover">
        <div class="word-english">{{ currentWord?.english }}</div>
        <el-divider />
        <div class="word-chinese">{{ currentWord?.chinese }}</div>
      </el-card>

      <div class="action-buttons">
        <el-button
          size="large"
          type="danger"
          :loading="submitting"
          @click="handleResult(1)"
          style="width: 45%"
        >
          😕 不认识
        </el-button>
        <el-button
          size="large"
          type="success"
          :loading="submitting"
          @click="handleResult(3)"
          style="width: 45%"
        >
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
import { getNewWords, submitResult, type WordData } from '@/api/study'

const loading = ref(true)
const submitting = ref(false)
const finished = ref(false)
const noWords = ref(false)
const errorMsg = ref('')

const words = ref<WordData[]>([])
const currentIndex = ref(0)
const knownCount = ref(0)
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
  noWords.value = false
  finished.value = false
  try {
    const res = await getNewWords()
    words.value = res.data.data
    totalCount.value = words.value.length
    currentIndex.value = 0
    knownCount.value = 0
    unknownCount.value = 0
    if (words.value.length === 0) {
      noWords.value = true
      errorMsg.value = '词库中没有单词数据'
    }
  } catch (e: any) {
    noWords.value = true
    errorMsg.value = e.response?.data?.message || '获取单词失败'
  } finally {
    loading.value = false
  }
}

async function handleResult(familiarity: number) {
  if (!currentWord.value || submitting.value) return

  submitting.value = true
  try {
    await submitResult(currentWord.value.id, familiarity)
    if (familiarity === 3) {
      knownCount.value++
    } else {
      unknownCount.value++
    }
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

function startNewRound() {
  loadWords()
}

onMounted(loadWords)
</script>

<style scoped>
.study-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8edf5 0%, #f0f4f8 100%);
}

.study-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 16px;
}

.study-card-area {
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
  font-size: 14px;
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
  justify-content: space-between;
  gap: 16px;
}
</style>
