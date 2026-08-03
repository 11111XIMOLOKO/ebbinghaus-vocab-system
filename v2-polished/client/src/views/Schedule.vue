<template>
  <div style="max-width:520px; margin:0 auto; padding:24px 0">
    <div v-if="loading" class="center"><span class="muted">加载复习任务...</span></div>

    <div v-else-if="noTasks" class="center">
      <el-result icon="success" title="暂无待复习单词">
        <template #extra><el-button type="primary" @click="$router.push('/')">返回首页</el-button></template>
      </el-result>
    </div>

    <div v-else-if="finished" class="center">
      <el-result icon="success" title="复习完成！">
        <template #sub-title>认识 {{ known }} | 模糊 {{ fuzzy }} | 不认识 {{ unknown }}</template>
        <template #extra><el-button type="primary" @click="$router.push('/')">返回首页</el-button></template>
      </el-result>
    </div>

    <div v-else>
      <!-- 进度条 -->
      <div style="display:flex; align-items:center; gap:12px; margin-bottom:24px; color:#606266">
        <span>复习 {{ idx + 1 }} / {{ total }}</span>
        <el-progress :percentage="Math.round((idx/total)*100)" :show-text="false" style="flex:1" />
      </div>

      <!-- 单词卡片 -->
      <el-card class="stat-card" style="text-align:center; min-height:260px; display:flex; flex-direction:column; justify-content:center">
        <div style="margin-bottom:12px">
          <el-tag v-if="current?.stage === 0" type="info" size="small">首次复习</el-tag>
          <el-tag v-else-if="current?.stage && current.stage >= 7" type="success" size="small">最后一轮</el-tag>
          <el-tag v-else-if="current?.stage" type="warning" size="small">第 {{ current.stage }} 轮复习</el-tag>
        </div>
        <div style="font-size:36px; font-weight:700; color:var(--text); padding:10px 0">{{ current?.english }}</div>
        <el-divider />
        <div style="font-size:22px; color:var(--muted); padding:0 0 10px">{{ current?.chinese }}</div>
      </el-card>

      <!-- 三按钮 -->
      <div style="margin-top:32px; display:flex; gap:12px">
        <el-button size="large" type="danger" :loading="sub" @click="handle(1)" style="flex:1">😕 不认识</el-button>
        <el-button size="large" type="warning" :loading="sub" @click="handle(2)" style="flex:1">🤔 模糊</el-button>
        <el-button size="large" type="success" :loading="sub" @click="handle(3)" style="flex:1">😊 认识</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDueReviews } from '@/api/schedule'
import { submitResult, type WordData } from '@/api/study'

const INTERVAL_LABELS = ['5 分钟', '30 分钟', '12 小时', '1 天', '2 天', '4 天', '7 天', '15 天']

const loading = ref(true); const sub = ref(false); const finished = ref(false); const noTasks = ref(false)
const words = ref<WordData[]>([]); const idx = ref(0)
const known = ref(0); const fuzzy = ref(0); const unknown = ref(0); const total = ref(0)
const current = computed(() => idx.value < words.value.length ? words.value[idx.value] : null)

async function load() {
  try {
    const r = await getDueReviews(); words.value = r.data.data; total.value = words.value.length
    idx.value = 0; known.value = 0; fuzzy.value = 0; unknown.value = 0
    if (words.value.length === 0) noTasks.value = true
  } catch { ElMessage.error('加载失败'); noTasks.value = true }
  finally { loading.value = false }
}

function stageName(s: number) { return s >= 7 ? '最后一轮' : `第 ${s} 轮` }

async function handle(f: number) {
  if (!current.value || sub.value) return; sub.value = true
  try {
    await submitResult(current.value.id, f)
    const s = current.value.stage ?? 0

    // 计算反馈信息
    let newStage: number; let msg: string
    if (f === 3) { newStage = Math.min(s + 1, 7); msg = `认识！已进入${stageName(newStage)}，${INTERVAL_LABELS[newStage]}后再见` }
    else if (f === 2) { newStage = Math.max(s - 1, 0); msg = `模糊，回退到${stageName(newStage)}` }
    else { newStage = 0; msg = '不认识，重置为首次复习' }

    // 更新当前词的 stage 给卡片显示用
    if (current.value) current.value.stage = newStage

    if (f === 3) { ElMessage.success(msg); known.value++ }
    else if (f === 2) { ElMessage.warning(msg); fuzzy.value++ }
    else { ElMessage.error(msg); unknown.value++ }

    setTimeout(() => {
      idx.value++
      if (idx.value >= total.value) finished.value = true
      sub.value = false
    }, 600)
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '提交失败'); sub.value = false }
}

onMounted(load)
</script>

<style scoped>
.center { display:flex; flex-direction:column; align-items:center; justify-content:center; min-height:60vh; gap:16px; }
</style>
