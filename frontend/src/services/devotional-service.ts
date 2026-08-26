import { apiDelete, apiGet, apiPost, apiPut } from '@/services/http'
import type { Devotional, SaveDevotionalInput } from '@/types/devotional'

export function listDevotionals() {
  return apiGet<Devotional[]>('/api/devotionals', { auth: true })
}

export function createDevotional(input: SaveDevotionalInput) {
  return apiPost<Devotional>('/api/devotionals', input, { auth: true })
}

export function updateDevotional(id: string, input: SaveDevotionalInput) {
  return apiPut<Devotional>(`/api/devotionals/${id}`, input, { auth: true })
}

export function deleteDevotional(id: string) {
  return apiDelete(`/api/devotionals/${id}`, { auth: true })
}
