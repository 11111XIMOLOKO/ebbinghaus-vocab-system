<template>
  <div class="stat-container">
    <el-container>
      <el-header class="page-header">
        <h2>📊 学习统计</h2>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>

      <el-main v-loading="loading">
        <!-- 概览卡片 -->
        <el-row :gutter="16" class="kpi-row">
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.totalStudyDays }}</div>
              <div class="kpi-label">累计学习天数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.streakDays }}</div>
              <div class="kpi-label">连续打卡天数 🔥</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.totalMastered }}</div>
              <div class="kpi-label">已掌握单词</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="kpi-card">
              <div class="kpi-num">{{ overview.totalWords }}</div>
              <div class="kpi-label">学习中单词</div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 阶段分布 -->
        <el-card class="section-card">
          <template #header>📈 各阶段单词分布</template>
          <div ref="stageChart" style="height: 300px" />
        </el-card>

        <!-- 每日趋势 -->
        <el-card class="section-card">
          <template #header>📅 每日学习趋势（近 30 天）</template>
          <div ref="trendChart" style="height: 300px" />
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getStatisticsOverview, getTrend, type StatOverview, type TrendItem } from '@/api/statistics'

type EChartsType = ReturnType<typeof echarts.init>

const loading = ref(true)
const stageChart = ref<HTMLElement | null>(null)
const trendChart = ref<HTMLElement | null>(null)
let stageInstance: EChartsType | null = null
let trendInstance: EChartsType | null = null

const overview = ref<StatOverview>({
  totalStudyDays: 0, streakDays: 0, totalMastered: 0, totalWords: 0,
  stageDistribution: [],
})

const stages = ['阶段0', '阶段1', '阶段2', '阶段3', '阶段4', '阶段5', '阶段6', '已掌握']

function renderStageChart(dist: number[]) {
  if (!stageChart.value) return
  if (!stageInstance) stageInstance = echarts.init(stageChart.value)
  stageInstance.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: dist.map((v, i) => ({ name: stages[i], value: v })),
      emphasis: { itemStyle: { shadowBlur: 10 } },
    }],
  })
}

function renderTrendChart(data: TrendItem[]) {
  if (!trendChart.value) return
  if (!trendInstance) trendInstance = echarts.init(trendChart.value)
  trendInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新学', '复习', '掌握'] },
    xAxis: { type: 'category', data: data.map(d => d.date.substring(5)) },
    yAxis: { type: 'value' },
    series: [
      { name: '新学', type: 'line', data: data.map(d => d.newWords), smooth: true },
      { name: '复习', type: 'line', data: data.map(d => d.reviewWords), smooth: true },
      { name: '掌握', type: 'line', data: data.map(d => d.masteredWords), smooth: true },
    ],
  })
}

onMounted(async () => {
  try {
    const [ov, tr] = await Promise.all([getStatisticsOverview(), getTrend()])
    overview.value = ov.data.data
    await nextTick()
    renderStageChart(ov.data.data.stageDistribution || [])
    renderTrendChart(tr.data.data)
  } catch (e) {
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.stat-container { min-height: 100vh; background: #f5f7fa; }
.page-header { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 0 24px; height: 64px; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.page-header h2 { margin: 0; }
.kpi-row { margin-bottom: 16px; }
.kpi-card { text-align: center; }
.kpi-num { font-size: 32px; font-weight: 700; color: #409eff; }
.kpi-label { color: #909399; margin-top: 4px; }
.section-card { margin-bottom: 16px; }
</style>
