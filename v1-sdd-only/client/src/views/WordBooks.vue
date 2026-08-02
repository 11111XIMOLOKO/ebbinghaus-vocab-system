<template>
  <div class="wordbooks-container">
    <el-container>
      <el-header class="page-header">
        <div class="header-left">
          <h2>📚 词库列表</h2>
          <span class="header-tip">选择一个词库开始学习</span>
        </div>
        <div class="header-right">
          <el-button @click="$router.push('/')">返回首页</el-button>
        </div>
      </el-header>

      <el-main>
        <el-row :gutter="20" v-loading="loading">
          <el-col v-for="book in books" :key="book.id" :xs="24" :sm="12" :md="8" :lg="6">
            <el-card class="book-card" shadow="hover" @click="handleSelect(book)">
              <div class="book-cover">
                <span class="book-emoji">{{ getEmoji(book.sortOrder) }}</span>
              </div>
              <h3 class="book-name">{{ book.name }}</h3>
              <p class="book-desc">{{ book.description }}</p>
              <div class="book-meta">
                <el-tag size="small" type="info">{{ book.wordCount }} 词</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && books.length === 0" description="暂无词库" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWordBooks, type WordBookData } from '@/api/wordBook'

const books = ref<WordBookData[]>([])
const loading = ref(true)

function getEmoji(sortOrder: number): string {
  const emojis: Record<number, string> = {
    1: '🏫', 2: '🎓', 3: '📖', 4: '📘', 5: '📝', 6: '✈️', 7: '🌟',
  }
  return emojis[sortOrder] || '📚'
}

function handleSelect(book: WordBookData) {
  ElMessage.info(`已选择「${book.name}」— 选词库功能将在 T21 学习计划中实现`)
  // T21: 调用 study-plan API 设置当前词库
}

onMounted(async () => {
  try {
    const res = await getWordBooks()
    books.value = res.data.data
  } catch (e) {
    ElMessage.error('获取词库列表失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.wordbooks-container {
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

.header-left h2 {
  margin: 0;
  display: inline;
}

.header-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 14px;
}

.book-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-4px);
}

.book-cover {
  text-align: center;
  padding: 24px 0 12px;
}

.book-emoji {
  font-size: 48px;
}

.book-name {
  text-align: center;
  margin: 0 0 8px;
  color: #303133;
}

.book-desc {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin: 0 0 12px;
  min-height: 32px;
}

.book-meta {
  text-align: center;
}
</style>
