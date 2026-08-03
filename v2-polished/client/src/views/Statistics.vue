<template>
  <div class="page-grid">
    <!-- KPI 卡片 -->
    <el-row :gutter="18">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <el-card class="stat-card">
          <div class="muted">{{ item.label }}</div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="muted">{{ item.tip }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 + 薄弱分析饼图 -->
    <el-row :gutter="18">
      <el-col :span="16">
        <el-card class="stat-card">
          <template #header>
            <div style="display:flex; justify-content:space-between; align-items:center">
              <strong>学习成长趋势</strong>
              <el-radio-group v-model="days" size="small" @change="loadTrend">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
                <el-radio-button :value="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" style="height:400px" />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <template #header><strong>薄弱词分析</strong></template>
          <div ref="weakChartRef" style="height:380px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 仪表盘指标 -->
    <el-card class="stat-card">
      <template #header><strong>关键学习指标</strong></template>
      <el-row :gutter="18">
        <el-col :span="8">
          <el-progress type="dashboard" :percentage="overview?.masteryRate || 0" :color="colors">
            <template #default><strong>{{ overview?.masteryRate || 0 }}%</strong><span>掌握率</span></template>
          </el-progress>
        </el-col>
        <el-col :span="8">
          <el-progress type="dashboard" :percentage="overview?.forgettingRate || 0" status="warning">
            <template #default><strong>{{ overview?.forgettingRate || 0 }}%</strong><span>遗忘率</span></template>
          </el-progress>
        </el-col>
        <el-col :span="8">
          <el-progress type="dashboard" :percentage="overview?.reviewCompletionRate || 0" status="success">
            <template #default><strong>{{ overview?.reviewCompletionRate || 0 }}%</strong><span>复习完成率</span></template>
          </el-progress>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsOverview, getTrend, getWeakAnalysis, type StatOverview, type TrendItem, type WeakItem } from '@/api/statistics'

// 状态
const overview = ref<StatOverview>()
const trend = ref<TrendItem[]>([])
const weakData = ref<WeakItem[]>([])
const days = ref(7)
const trendChartRef = ref<HTMLDivElement>()
const weakChartRef = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | undefined
let weakChart: echarts.ECharts | undefined

const colors = [{ color: '#f5222d', percentage: 30 }, { color: '#faad14', percentage: 60 }, { color: '#52c41a', percentage: 100 }]

const statCards = computed(() => [
  { label: '已学习单词', value: overview.value?.learnedWordCount || 0, tip: '累计进入复习计划' },
  { label: '已掌握单词', value: overview.value?.masteredWordCount || 0, tip: `掌握率 ${overview.value?.masteryRate || 0}%` },
  { label: '待复习任务', value: overview.value?.dueReviewCount || 0, tip: `完成率 ${overview.value?.reviewCompletionRate || 0}%` },
  { label: '薄弱生词', value: overview.value?.wrongWordCount || 0, tip: `遗忘率 ${overview.value?.forgettingRate || 0}%` },
])

function resizeCharts() { trendChart?.resize(); weakChart?.resize() }

async function loadOverview() {
  const res = await getStatisticsOverview(); overview.value = res.data.data
}

async function loadTrend() {
  const res = await getTrend(days.value); trend.value = res.data.data; await nextTick(); renderTrend()
}

async function loadWeak() {
  const res = await getWeakAnalysis(); weakData.value = res.data.data; await nextTick(); renderWeak()
}

function renderTrend() {
  if (!trendChartRef.value) return
  const labelInterval = days.value > 30 ? Math.ceil(days.value / 15) : 0
  trendChart = trendChart || echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新学单词', '复习单词', '已掌握', '错词'], bottom: 0 },
    grid: { left: 50, right: 30, top: 20, bottom: 60 },
    xAxis: { type: 'category', boundaryGap: false,
      data: trend.value.map(d => d.date.slice(5)),
      axisLabel: { rotate: days.value > 30 ? 45 : 0, interval: labelInterval } },
    yAxis: { type: 'value', name: '单词数', min: 0, minInterval: 1 },
    series: [
      { name: '新学单词', type: 'line', smooth: true, data: trend.value.map(d => d.newWords) },
      { name: '复习单词', type: 'line', smooth: true, data: trend.value.map(d => d.reviewWords) },
      { name: '已掌握', type: 'bar', stack: 'status', barWidth: 28, data: trend.value.map(d => d.masteredWords) },
      { name: '错词', type: 'bar', stack: 'status', barWidth: 28, data: trend.value.map(d => d.wrongWords) },
    ],
    color: ['#1890ff', '#52c41a', '#202124', '#f5222d', '#faad14'],
  })
}

function renderWeak() {
  if (!weakChartRef.value) return
  weakChart = weakChart || echarts.init(weakChartRef.value)
  weakChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['42%', '68%'],
      data: weakData.value.map(d => ({ name: d.category, value: d.count })),
      label: { formatter: '{b}\n{c}个' },
      color: ['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2', '#eb2f96'],
    }],
  })
}

onMounted(async () => {
  await Promise.all([loadOverview(), loadTrend(), loadWeak()])
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  trendChart?.dispose(); weakChart?.dispose()
})
</script>

<style scoped>
:deep(.el-progress__text) { display:flex; flex-direction:column; gap:6px; }
</style>
