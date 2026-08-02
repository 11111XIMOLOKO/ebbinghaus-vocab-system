import request from './request'

export interface StudyOverviewData {
  newWordCount: number
  reviewWordCount: number
  hasBook: boolean
  bookName: string
  checkedIn: boolean
  totalMastered: number
}

export interface WordData {
  id: number
  english: string
  chinese: string
}

export function getStudyOverview() {
  return request.get<{ code: number; message: string; data: StudyOverviewData }>('/study/overview')
}

export function getNewWords() {
  return request.post<{ code: number; message: string; data: WordData[] }>('/study/new')
}

export function submitResult(wordId: number, familiarity: number) {
  return request.post<{ code: number; message: string }>('/study/submit', {
    wordId,
    familiarity,
  })
}
