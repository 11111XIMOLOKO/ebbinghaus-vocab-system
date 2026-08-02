import request from './request'

export interface ScheduleOverviewData {
  pendingCount: number
  totalCount: number
  stageDistribution: number[]
}

export interface WordData {
  id: number
  english: string
  chinese: string
}

export function getScheduleOverview() {
  return request.get<{ code: number; message: string; data: ScheduleOverviewData }>('/schedule/overview')
}

export function getDueReviews() {
  return request.get<{ code: number; message: string; data: WordData[] }>('/schedule/due')
}

export function getScheduleGoal() {
  return request.get<{ code: number; message: string; data: { planWordCount: number; reviewMultiplier: number; dailyReviewCount: number; dailyTotalCount: number } }>('/schedule/goal')
}
