import request from './request'

export interface StatOverview {
  totalStudyDays: number
  streakDays: number
  totalMastered: number
  totalWords: number
  stageDistribution: number[]
}

export interface TrendItem {
  date: string
  newWords: number
  reviewWords: number
  masteredWords: number
}

export interface WeakItem {
  wordId: number
  word: string
  wrongCount: number
}

export function getStatisticsOverview() {
  return request.get<{ code: number; message: string; data: StatOverview }>('/statistics/overview')
}

export function getTrend() {
  return request.get<{ code: number; message: string; data: TrendItem[] }>('/statistics/trend')
}

export function getWeakAnalysis() {
  return request.get<{ code: number; message: string; data: WeakItem[] }>('/statistics/weak-analysis')
}
