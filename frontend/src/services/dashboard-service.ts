import { apiGet } from '@/services/http'
import type { Dashboard } from '@/types/dashboard'

export function getDashboard() {
  return apiGet<Dashboard>('/api/dashboard', { auth: true })
}
