<template>
  <div style="max-width:520px; margin:0 auto; padding:24px 0">
    <div v-if="loading" class="center"><span class="muted">正在准备单词...</span></div>

    <div v-else-if="finished" class="center">
      <el-result icon="success" title="学习完成！">
        <template #sub-title>认识 {{ known }} 个 | 不认识 {{ unknown }} 个</template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/schedule')">🔄 去复习刚学的词</el-button>
          <el-button @click="$router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>

    <div v-else-if="noWords" class="center">
      <el-result icon="info" :title="errMsg">
        <template #extra><el-button type="primary" @click="$router.push('/word-books')">去选词库</el-button></template>
      </el-result>
    </div>

    <div v-else>
      <div style="display:flex; align-items:center; gap:12px; margin-bottom:24px; color:#606266">
        <span>第 {{ idx + 1 }} / {{ total }} 个</span>
        <el-progress :percentage="Math.round((idx/total)*100)" :show-text="false" style="flex:1" />
      </div>
      <el-card class="stat-card" style="text-align:center; min-height:240px; display:flex; flex-direction:column; justify-content:center">
        <div style="font-size:36px; font-weight:700; color:var(--text); padding:20px 0 10px">{{ current?.english }}</div>
        <el-divider />
        <div style="font-size:22px; color:var(--muted); padding:10px 0 20px">{{ current?.chinese }}</div>
      </el-card>
      <div style="margin-top:32px; display:flex; gap:12px">
        <el-button size="large" type="danger" :loading="sub" @click="handle(1)" style="flex:1">😕 不认识</el-button>
        <el-button size="large" type="success" :loading="sub" @click="handle(3)" style="flex:1">😊 认识</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNewWords, submitResult, type WordData } from '@/api/study'

const loading = ref(true); const sub = ref(false); const finished = ref(false); const noWords = ref(false)
const errMsg = ref(''); const words = ref<WordData[]>([]); const idx = ref(0)
const known = ref(0); const unknown = ref(0); const total = ref(0)
const current = computed(() => idx.value < words.value.length ? words.value[idx.value] : null)

async function load() {
  try {
    const r = await getNewWords(); words.value = r.data.data; total.value = words.value.length
    idx.value = 0; known.value = 0; unknown.value = 0
    if (words.value.length === 0) { noWords.value = true; errMsg.value = '词库中没有单词' }
  } catch (e: any) { noWords.value = true; errMsg.value = e.response?.data?.message || '获取失败' }
  finally { loading.value = false }
}

async function handle(f: number) {
  if (!current.value || sub.value) return; sub.value = true
  try {
    await submitResult(current.value.id, f)
    f === 3 ? known.value++ : unknown.value++; idx.value++
    if (idx.value >= total.value) finished.value = true
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '提交失败') }
  finally { sub.value = false }
}

onMounted(load)
</script>

<style scoped>
.center { display:flex; flex-direction:column; align-items:center; justify-content:center; min-height:60vh; gap:16px; }
</style>
