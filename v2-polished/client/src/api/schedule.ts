import request from './request'
import type { WordData } from './study'

export function getDueReviews() {
  return request.get<{ code: number; message: string; data: WordData[] }>('/schedule/due')
}
