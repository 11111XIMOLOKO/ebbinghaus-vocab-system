import request from './request'

export interface WordData { id: number; english: string; chinese: string; stage?: number; familiarity?: number }

export function getNewWords() {
  return request.post<{ code: number; message: string; data: WordData[] }>('/study/new')
}

export function submitResult(wordId: number, familiarity: number) {
  return request.post<{ code: number; message: string }>('/study/submit', { wordId, familiarity })
}
