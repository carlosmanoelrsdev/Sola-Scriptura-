import { clearAuthToken } from '@/services/auth-token'
import { apiDelete, apiGet, apiPut } from '@/services/http'
import type { UpdateUserProfileInput, UserProfile } from '@/types/user'

export function getCurrentUser() {
  return apiGet<UserProfile>('/api/users/me', { auth: true })
}

export function updateCurrentUser(input: UpdateUserProfileInput) {
  return apiPut<UserProfile>('/api/users/me', input, { auth: true })
}

export function deleteCurrentUser() {
  return apiDelete('/api/users/me', { auth: true })
}

export function logout() {
  clearAuthToken()
}
