<template>
  <div class="page-grid">
    <el-row :gutter="18" v-loading="loading">
      <el-col v-for="book in books" :key="book.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="stat-card" style="text-align:center; cursor:pointer" @click="handleSelect(book)">
          <div style="font-size:48px; padding:16px 0 8px">{{ emojis[book.sortOrder] || '📚' }}</div>
          <h3 style="margin:0 0 8px">{{ book.name }}</h3>
          <p class="muted" style="font-size:13px; margin:0 0 12px">{{ book.description }}</p>
          <el-tag size="small">{{ book.wordCount }} 词</el-tag>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && books.length === 0" description="暂无词库" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWordBooks, type WordBookData } from '@/api/wordBook'
import { updateStudyPlan } from '@/api/studyPlan'

const router = useRouter()
const books = ref<WordBookData[]>([]); const loading = ref(true)
const emojis: Record<number,string> = {1:'🏫',2:'🎓',3:'📖',4:'📘',5:'📝',6:'✈️',7:'🌟'}

async function handleSelect(book: WordBookData) {
  try {
    await updateStudyPlan({ bookId: book.id })
    ElMessage.success(`已选择「${book.name}」`)
    router.push('/study')
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '设置失败') }
}

onMounted(async () => {
  try { const r = await getWordBooks(); books.value = r.data.data }
  catch { ElMessage.error('获取词库列表失败') }
  finally { loading.value = false }
})
</script>
