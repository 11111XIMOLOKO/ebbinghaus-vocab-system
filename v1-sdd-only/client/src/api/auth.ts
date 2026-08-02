import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
}

export interface LoginData {
  token: string
  userId: number
  username: string
  role: string
}

export interface UserInfo {
  id: number
  username: string
  role: string
  status: number
  createdAt: string
}

export function login(data: LoginRequest) {
  return request.post<{ code: number; message: string; data: LoginData }>('/auth/login', data)
}

export function register(data: RegisterRequest) {
  return request.post<{ code: number; message: string }>('/auth/register', data)
}

export function getMe() {
  return request.get<{ code: number; message: string; data: UserInfo }>('/auth/me')
}

export function logout() {
  return request.post<{ code: number; message: string }>('/auth/logout')
}
