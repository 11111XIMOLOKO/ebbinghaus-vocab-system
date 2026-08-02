<template>
  <div class="plan-container">
    <el-container>
      <el-header class="page-header">
        <h2>⚙️ 学习计划</h2>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>

      <el-main v-loading="loading">
        <el-card class="plan-card">
          <template #header><strong>每日学习设置</strong></template>

          <el-form :model="form" label-width="140px" size="large">
            <el-form-item label="选择词库">
              <el-select v-model="form.bookId" placeholder="请选择词库" style="width: 100%">
                <el-option
                  v-for="book in books"
                  :key="book.id"
                  :label="`${book.name} (${book.wordCount}词)`"
                  :value="book.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="每日新词数">
              <el-input-number v-model="form.planWordCount" :min="1" :max="100" />
              <span class="form-tip">每天学多少个新单词</span>
            </el-form-item>

            <el-form-item label="复习倍数">
              <el-input-number v-model="form.reviewMultiplier" :min="1" :max="10" />
              <span class="form-tip">每日复习数 = 新词数 × 倍数</span>
            </el-form-item>

            <el-divider />

            <el-form-item label="每日新词">
              <el-tag type="primary" size="large">{{ form.planWordCount }} 个</el-tag>
            </el-form-item>
            <el-form-item label="每日复习">
              <el-tag type="warning" size="large">{{ form.planWordCount * form.reviewMultiplier }} 个</el-tag>
            </el-form-item>
            <el-form-item label="每日总任务">
              <el-tag type="success" size="large">{{ form.planWordCount + form.planWordCount * form.reviewMultiplier }} 个</el-tag>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudyPlan, updateStudyPlan, type StudyPlanData } from '@/api/studyPlan'
import { getWordBooks, type WordBookData } from '@/api/wordBook'

const loading = ref(true)
const saving = ref(false)
const books = ref<WordBookData[]>([])

const form = reactive({
  bookId: null as number | null,
  planWordCount: 10,
  reviewMultiplier: 1,
})

onMounted(async () => {
  try {
    const [planRes, bookRes] = await Promise.all([getStudyPlan(), getWordBooks()])
    const plan: StudyPlanData = planRes.data.data
    form.bookId = plan.bookId
    form.planWordCount = plan.planWordCount
    form.reviewMultiplier = plan.reviewMultiplier
    books.value = bookRes.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  try {
    await updateStudyPlan({
      bookId: form.bookId ?? undefined,
      planWordCount: form.planWordCount,
      reviewMultiplier: form.reviewMultiplier,
    })
    ElMessage.success('设置已保存')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.plan-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.page-header h2 {
  margin: 0;
}

.plan-card {
  max-width: 560px;
  margin: 24px auto;
}

.form-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}
</style>
