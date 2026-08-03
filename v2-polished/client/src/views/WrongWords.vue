<template>
  <div class="page-grid">
    <el-card class="stat-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="english" label="英文" min-width="150" />
        <el-table-column prop="chinese" label="中文" min-width="150" />
        <el-table-column prop="wrongCount" label="错误次数" width="100" align="center">
          <template #default="{row}"><el-tag :type="row.wrongCount>=3?'danger':'warning'">{{row.wrongCount}}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{row}">
            <el-button size="small" type="success" @click="handleMaster(row)">掌握</el-button>
            <el-button size="small" type="danger" @click="handleDel(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:center">
        <el-pagination v-model:current-page="pageNum" :total="total" :page-size="20" layout="total,prev,pager,next" @current-change="load" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

interface Item { id:number; english:string; chinese:string; wrongCount:number }
const loading = ref(false); const list = ref<Item[]>([])
const pageNum = ref(1); const total = ref(0)

async function load() {
  loading.value = true
  try {
    const r = await request.get('/wrong-words/page', { params: { pageNum: pageNum.value, pageSize: 20 } })
    list.value = r.data.data.records; total.value = r.data.data.total
  } catch { ElMessage.error('加载失败') } finally { loading.value = false }
}
async function handleMaster(row: Item) {
  try { await request.post(`/wrong-words/${row.id}/mastered`); ElMessage.success('已掌握'); load() }
  catch (e: any) { ElMessage.error(e.response?.data?.message || '失败') }
}
async function handleDel(row: Item) {
  try { await ElMessageBox.confirm('确定删除？','确认'); await request.delete(`/wrong-words/${row.id}`); ElMessage.success('已删除'); load() }
  catch { /* cancel */ }
}
onMounted(load)
</script>
