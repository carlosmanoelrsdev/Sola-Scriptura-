import { apiPost } from '@/services/http'
import type { LoginInput, LoginResponse, RegisterInput, RegisterResponse } from '@/types/auth'

export function login(input: LoginInput) {
  return apiPost<LoginResponse>('/api/auth/login', input)
}

export function register(input: RegisterInput) {
  return apiPost<RegisterResponse>('/api/auth/register', input)
}
