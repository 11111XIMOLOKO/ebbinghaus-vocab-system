import request from './request'

export interface WordBookData {
  id: number
  name: string
  description: string
  wordCount: number
  sortOrder: number
  createdAt: string
}

export function getWordBooks() {
  return request.get<{ code: number; message: string; data: WordBookData[] }>('/word-books')
}
