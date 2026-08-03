import request from './request'

export interface LoginData { token: string; userId: number; username: string; role: string }

export function login(data: { username: string; password: string }) {
  return request.post<{ code: number; message: string; data: LoginData }>('/auth/login', data)
}

export function register(data: { username: string; password: string }) {
  return request.post<{ code: number; message: string }>('/auth/register', data)
}

export function getMe() {
  return request.get<{ code: number; message: string; data: any }>('/auth/me')
}

export function logout() {
  return request.post<{ code: number; message: string }>('/auth/logout')
}
