import request from './request'

export interface StudyPlanData {
  id: number; userId: number; bookId: number | null;
  planWordCount: number; reviewMultiplier: number;
  dailyReviewCount: number; dailyTotalCount: number;
}

export function getStudyPlan() {
  return request.get<{ code: number; message: string; data: StudyPlanData }>('/study-plan')
}

export function updateStudyPlan(p: { bookId?: number; planWordCount?: number; reviewMultiplier?: number }) {
  return request.put<{ code: number; message: string; data: StudyPlanData }>('/study-plan', p)
}
