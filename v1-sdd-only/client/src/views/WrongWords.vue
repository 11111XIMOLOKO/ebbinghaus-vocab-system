<template>
  <div class="wrong-container">
    <el-container>
      <el-header class="page-header">
        <h2>📝 错词本</h2>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>

      <el-main>
        <el-table :data="list" v-loading="loading" stripe style="width: 100%">
          <el-table-column prop="english" label="英文" min-width="150" />
          <el-table-column prop="chinese" label="中文" min-width="150" />
          <el-table-column prop="wrongCount" label="错误次数" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.wrongCount >= 3" type="danger">{{ row.wrongCount }}</el-tag>
              <el-tag v-else type="warning">{{ row.wrongCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="handleMaster(row)">标记掌握</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="pageNum"
            :total="total"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            @current-change="loadData"
          />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

interface WrongWordItem {
  id: number
  wordId: number
  english: string
  chinese: string
  wrongCount: number
  status: number
}

const loading = ref(false)
const list = ref<WrongWordItem[]>([])
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/wrong-words/page', {
      params: { pageNum: pageNum.value, pageSize }
    })
    list.value = res.data.data.records
    total.value = res.data.data.total
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleMaster(row: WrongWordItem) {
  try {
    await request.post(`/wrong-words/${row.id}/mastered`)
    ElMessage.success('已标记为掌握')
    loadData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function handleDelete(row: WrongWordItem) {
  try {
    await ElMessageBox.confirm('确定删除该错词记录？', '确认')
    await request.delete(`/wrong-words/${row.id}`)
    ElMessage.success('已删除')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.wrong-container { min-height: 100vh; background: #f5f7fa; }
.page-header { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 0 24px; height: 64px; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.page-header h2 { margin: 0; }
.pagination { margin-top: 16px; display: flex; justify-content: center; }
</style>
