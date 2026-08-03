import request from './request'

export interface StatOverview {
  totalStudyDays: number; streakDays: number; totalMastered: number; totalWords: number;
  learnedWordCount: number; masteredWordCount: number; dueReviewCount: number; wrongWordCount: number;
  masteryRate: number; forgettingRate: number; reviewCompletionRate: number;
  stageDistribution: number[];
}

export interface TrendItem {
  date: string; newWords: number; reviewWords: number;
  masteredWords: number; wrongWords: number; studyDuration: number;
}

export interface WeakItem { category: string; count: number }

export function getStatisticsOverview() {
  return request.get<{ code: number; message: string; data: StatOverview }>('/statistics/overview')
}

export function getTrend(days: number = 30) {
  return request.get<{ code: number; message: string; data: TrendItem[] }>('/statistics/trend', { params: { days } })
}

export function getWeakAnalysis() {
  return request.get<{ code: number; message: string; data: WeakItem[] }>('/statistics/weak-analysis')
}
