<template>
  <div class="page-grid">
    <el-card class="stat-card" style="max-width:560px" v-loading="loading">
      <template #header><strong>每日学习设置</strong></template>
      <el-form label-width="120px" size="large">
        <el-form-item label="选择词库">
          <el-select v-model="form.bookId" placeholder="请选择词库" style="width:100%">
            <el-option v-for="b in books" :key="b.id" :label="`${b.name} (${b.wordCount}词)`" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="每日新词数">
          <el-input-number v-model="form.planWordCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="复习倍数">
          <el-input-number v-model="form.reviewMultiplier" :min="1" :max="10" />
        </el-form-item>
        <el-divider />
        <el-form-item label="每日新词"><el-tag size="large">{{ form.planWordCount }} 个</el-tag></el-form-item>
        <el-form-item label="每日复习"><el-tag type="warning" size="large">{{ form.planWordCount * form.reviewMultiplier }} 个</el-tag></el-form-item>
        <el-form-item label="每日总任务"><el-tag type="success" size="large">{{ form.planWordCount + form.planWordCount * form.reviewMultiplier }} 个</el-tag></el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudyPlan, updateStudyPlan, type StudyPlanData } from '@/api/studyPlan'
import { useRouter } from 'vue-router'
import { getWordBooks, type WordBookData } from '@/api/wordBook'

const router = useRouter()
const loading = ref(true); const saving = ref(false); const books = ref<WordBookData[]>([])
const form = reactive({ bookId: null as number|null, planWordCount: 10, reviewMultiplier: 1 })

onMounted(async () => {
  try {
    const [p, b] = await Promise.all([getStudyPlan(), getWordBooks()])
    const d: StudyPlanData = p.data.data
    form.bookId = d.bookId; form.planWordCount = d.planWordCount; form.reviewMultiplier = d.reviewMultiplier
    books.value = b.data.data
  } catch { ElMessage.error('加载失败') } finally { loading.value = false }
})

async function handleSave() {
  saving.value = true
  try {
    await updateStudyPlan({ bookId: form.bookId??undefined, planWordCount: form.planWordCount, reviewMultiplier: form.reviewMultiplier })
    ElMessage.success('设置已保存，即将跳转')
    setTimeout(() => router.push('/study'), 800)
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '保存失败') }
  finally { saving.value = false }
}
</script>
